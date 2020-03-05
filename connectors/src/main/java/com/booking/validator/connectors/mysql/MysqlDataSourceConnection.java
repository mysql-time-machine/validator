package com.booking.validator.connectors.mysql;

import com.booking.validator.connectors.DataSourceConnection;
import com.booking.validator.data.Data;

import com.booking.validator.data.source.DataSourceQueryOptions;
import com.booking.validator.data.source.mysql.MysqlQueryOptions;
import com.booking.validator.utils.HexEncoder;
import org.apache.commons.dbcp2.BasicDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by dbatheja on 10/02/20.
 * Refactored and used @psalimov's existing MySQL-connector implementation
 *
 */
public class MysqlDataSourceConnection implements DataSourceConnection {
    static final Logger LOGGER = LoggerFactory.getLogger(MysqlDataSourceConnection.class);
    private enum Property implements com.booking.validator.utils.Property {
        HOST("host"), PORT("port"), SCHEMA("schema"), USERNAME("username"), PASSWORD("password");
        private final String name;
        Property(String name) { this.name = name; }
        @Override
        public String getName(){ return name; }
    }

    private BasicDataSource source;
    private String quote;

    public MysqlDataSourceConnection(Map<String, String> config) {
        initDataSource(config);
        initQuote();

    }

    public void initDataSource(Map<String, String> config) {
        source = new BasicDataSource();
        source.setDriverClassName("com.mysql.jdbc.Driver");
        source.setUsername(Property.USERNAME.value(config));
        source.setPassword(Property.PASSWORD.value(config));
        source.setUrl( String.format("jdbc:mysql://%s:%s/%s", Property.HOST.value(config), Property.PORT.value(config), Property.SCHEMA.value(config)) );
        source.addConnectionProperty("useUnicode", "true");
        source.addConnectionProperty("characterEncoding", "UTF-8");
        source.addConnectionProperty("zeroDateTimeBehavior", "convertToNull");
        source.addConnectionProperty("serverTimezone","Europe/Amsterdam");
        source.addConnectionProperty("yearIsDateType","false");
        source.addConnectionProperty("tinyInt1isBit", "false");
    }

    public void initQuote() throws RuntimeException {
        try (Connection connection = source.getConnection()) {
            quote = connection.getMetaData().getIdentifierQuoteString();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    private String buildSelectDoubleColumnQuery(String table, String condition, String quote){

        // this is to fetch float or double values having enough amount of decimal digits
        // IEEE 754 states that up to 17 significant digits may be required to preserve precision
        // in double->string->double conversion

        // NOTE: there is a bug in mysql rounding function so its second argument also messes up the result sometimes
        // bad values for the second argument:
        // 31 160.0
        // 32 505502.28
        // 33 7.104
        return String.format("SELECT ROUND("+quote("%%s",quote)+",17) FROM %s WHERE %s LIMIT 1;", table, condition);

    }

    private String quote(String string, String quote){
        return quote + string + quote;
    }

    private String buildSelectAllQuery(String table, String condition){

        return String.format("SELECT * FROM %s WHERE %s LIMIT 2;", table, condition);

    }

    private String buildQueryConditionPart(List<String> columns, String quote){
        return String.format("(%s) = (%s)",
                columns.stream().map(c -> quote(c, quote) ).collect(Collectors.joining(",")),
                Stream.generate( () -> "?" ).limit(columns.size()).collect(Collectors.joining(","))
        );
    }


    @Override
    public Data query(DataSourceQueryOptions options) {
        MysqlQueryOptions queryOptions = (MysqlQueryOptions) options;
        List<String> columns = new ArrayList<>();
        List<Object> values = new ArrayList<>();
        queryOptions.getPrimaryKeys().forEach((key, obj)->{
            columns.add(key);
            values.add(values);
        });
        String condition = buildQueryConditionPart(columns, quote);
        String selectAllQuery = buildSelectAllQuery(queryOptions.getTableName(), condition);
        String selectDoubleQuery = buildSelectDoubleColumnQuery(queryOptions.getTableName(), condition, quote);
        Iterable<Object> args = values;

        return resolve(selectAllQuery, selectDoubleQuery, args);
    }

    public Data resolve(String selectAllQuery, String selectDoubleQuery, Iterable<Object> args) {

        try (Connection connection = source.getConnection();
             PreparedStatement statement = connection.prepareStatement(selectAllQuery)){

            int i = 1;
            for (Object arg : args) statement.setObject(i++,arg);

            ResultSet row = statement.executeQuery(); // will be closed on statement closing

            if (!row.next()) return null;

            if (!row.isLast()) throw new RuntimeException("An ambiguous data pointer to mysql source {}, the query {}");

            ResultSetMetaData meta = row.getMetaData();

            int columnCount = meta.getColumnCount();

            MysqlCell[] cells = new MysqlCell[columnCount];

            for (int columnIndex=1; columnIndex <= columnCount; columnIndex++) {

                String column = meta.getColumnName(columnIndex);

                String type = meta.getColumnTypeName(columnIndex);

                Object value;

                switch (type){

                    case "DATE":

                        value = row.getObject(columnIndex);

                        // jdbc zeroDateTimeBehavior=convertToNull converts zero dates to nulls
                        // for non-zero dates we get a date object in the timezone of the database
                        // so calling toString on it may differ from the textual representation of this date as
                        // given by the db

                        // TODO: refactor this and the transformation to return date in the correct timezone

                        if (value != null) value = row.getString(columnIndex);

                        break;

                    // there is a bug in jdbc so it loses some precision when reading floating point types through
                    // ResultSet methods. Shortly, under the hood, it retrieves a decimal string representation of a
                    // value that is not long enough. Then it converts this string back to IEEE float/double losing
                    // some bits. As a workaround, we have to read these values using a separate query.
                    case "FLOAT":

                        value = readFloat(column, selectDoubleQuery, args);

                        break;

                    case "DOUBLE":

                        value = readDouble(column, selectDoubleQuery, args);

                        break;

                    case "TIMESTAMP":

                        value = row.getObject(columnIndex);

                        // jdbc zeroDateTimeBehavior=convertToNull does a good thing for us, but when it comes to
                        // timestamps we would like to preserve 0 as value

                        if (value == null && row.getString(columnIndex) != null) value = new Timestamp(0);

                        break;

                    default:

                        value = row.getObject(columnIndex);

                        break;
                }

                cells[columnIndex-1] = new MysqlCell(column, type, value);

            }

            return transform(cells);

        } catch (SQLException e) {

            LOGGER.error("Failed resolving a data pointer to mysql source {} using the query {} ", source, selectAllQuery, e);

            throw new RuntimeException(e);
        }

    }

    public Data transform(MysqlCell[] cells) {
        Map<String,Object> result = new HashMap<>();
        for (MysqlCell cell : cells){

            String column = cell.getColumn();

            Object rawValue = cell.getValue();

            Object value = null;

            String type = cell.getType();

            switch (type){

                case "DATETIME":

                case "DOUBLE":
                    value = rawValue;
                    break;

                case "FLOAT":
                    value = rawValue;
                    break;

                case "TIMESTAMP":
                    value = rawValue;
                    break;

                default:
                    if (rawValue instanceof byte[]){
                        value = HexEncoder.encode((byte[]) rawValue);
                    } else {
                        value = (rawValue == null ? null : rawValue.toString());
                    }
                    break;
            }
            result.put(column,value);
        }
        return new Data(result);
    }

    private Float readFloat(String column, String selectDoubleQuery, Iterable<Object> args){

        return readFloatingPointValueColumn(column, Float::valueOf, selectDoubleQuery, args);

    }

    private Double readDouble(String column, String selectDoubleQuery, Iterable<Object> args){

        return readFloatingPointValueColumn(column, Double::valueOf, selectDoubleQuery, args);

    }

    private <T> T readFloatingPointValueColumn(String column, Function<String, T> transformation, String selectDoubleQuery, Iterable<Object> args) {

        String query = String.format(selectDoubleQuery, column);

        try (Connection connection = source.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)){

            int i = 1;
            for (Object arg : args) statement.setObject(i++,arg);

            ResultSet row = statement.executeQuery(); // will be closed on statement closing

            if (!row.next()) return null;

            if (!row.isLast()) throw new RuntimeException("An ambiguous data pointer to mysql source {}, the query {}");

            String decimalString = row.getString(1);

            return decimalString == null ? null : transformation.apply(decimalString);

        } catch (SQLException e) {

            LOGGER.error("Failed reading of a floating point value from the mysql source {} using the query {} ", source, query, e);

            throw new RuntimeException(e);
        }
    }

    @Override
    public void close() {
        try (Connection connection = source.getConnection()) {
            connection.close();
        } catch (SQLException e) {
            LOGGER.error("Error while closing client connection: ", e.getStackTrace());
        }
    }

}

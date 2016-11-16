package com.booking.validator.data.mysql;

import com.booking.validator.data.Data;
import com.booking.validator.data.DataPointer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.*;
import java.util.function.Function;


/**
 * Created by psalimov on 10/19/16.
 */
public class MysqlDataPointer implements DataPointer {

    private static final Logger LOGGER = LoggerFactory.getLogger(MysqlDataPointer.class);

    private final DataSource source;

    private final String uri;
    private final String selectAllQuery;
    private final String selectDoubleQuery;
    private final Iterable<Object> args;
    private final Transformation transformation;

    public MysqlDataPointer(DataSource source, String selectAllQuery, String selectDoubleQuery, Iterable<Object> args, Transformation transformation, String uri) {
        this.source = source;
        this.selectAllQuery = selectAllQuery;
        this.args = args;
        this.transformation = transformation;
        this.selectDoubleQuery = selectDoubleQuery;
        this.uri = uri;
    }

    @Override
    public Data resolve() {

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

            for (int columnIndex=1; columnIndex <= columnCount; columnIndex++){

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

                        value = readFloat(column);

                        break;

                    case "DOUBLE":

                        value = readDouble(column);

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

            return transformation.transform(cells);

        } catch (SQLException e) {

            LOGGER.error("Failed resolving a data pointer to mysql source {} using the query {} ", source, selectAllQuery, e);

            throw new RuntimeException(e);
        }

    }

    private Float readFloat(String column){

        return readFloatingPointValueColumn(column, Float::valueOf);

    }

    private Double readDouble(String column){

        return readFloatingPointValueColumn(column, Double::valueOf);

    }

    private <T> T readFloatingPointValueColumn(String column, Function<String, T> transformation) {

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

    public String toString(){
        return uri;
    }


}

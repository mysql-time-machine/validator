package com.booking.validator.data.mysql;

import com.booking.validator.data.DataPointer;
import com.booking.validator.data.DataPointerFactory;
import org.apache.commons.dbcp2.BasicDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLDecoder;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by psalimov on 10/18/16.
 */
public class MysqlDataPointerFactory implements DataPointerFactory{

    private enum Property implements com.booking.validator.utils.Property {
        HOST("host"), PORT("port"), SCHEMA("schema"), USERNAME("username"), PASSWORD("password");

        private final String name;

        Property(String name) { this.name = name; }

        @Override
        public String getName(){ return name; }

    }

    private static final Logger LOGGER = LoggerFactory.getLogger(MysqlDataPointerFactory.class);

    private static final String MYSQL = "mysql";

    public static MysqlDataPointerFactory build(Map<String, Map<String,String>> properties){

        Map<String, DataSource> sources = new HashMap<>();

        for (Map.Entry<String, Map<String,String>> property : properties.entrySet()){

            Map<String,String> config = property.getValue();

            BasicDataSource source = new BasicDataSource();

            source.setDriverClassName("com.mysql.jdbc.Driver");
            source.setUsername(Property.USERNAME.value(config));
            source.setPassword(Property.PASSWORD.value(config));
            source.setUrl( String.format("jdbc:mysql://%s:%s/%s", Property.HOST.value(config), Property.PORT.value(config), Property.SCHEMA.value(config)) );
            source.addConnectionProperty("useUnicode", "true");
            source.addConnectionProperty("characterEncoding", "UTF-8");
            source.addConnectionProperty("zeroDateTimeBehavior", "convertToNull");
            source.addConnectionProperty("serverTimezone","Europe/Amsterdam");
            source.addConnectionProperty("yearIsDateType","false");

            sources.put(property.getKey(), source);

        }

        return new MysqlDataPointerFactory(sources);
    }

    private final Map<String,DataSource> sources;
    private final Map<String,String> quotes = new HashMap<>();

    public MysqlDataPointerFactory(Map<String, DataSource> sources) {

        this.sources = sources;

    }

    private String decodeQueryToken(String encoded){
        try {
            return URLDecoder.decode(encoded,"UTF-8");
        } catch (UnsupportedEncodingException e) {
            LOGGER.error("UTF-8 not supported?", e);
            throw new RuntimeException(e);
        }
    }

    public DataPointer produce(String uriString, Map<String, Object> transformations) {

        URI uri = URI.create(uriString);

        String sourceName = uri.getHost();

        DataSource source = sources.get(sourceName);

        if (source == null) throw new RuntimeException("No source found for name: " + sourceName);

        String table = uri.getPath().split("/")[1];

        List<String[]> args = Arrays.stream(uri.getQuery().split("&")).map(s->s.split("=")).collect(Collectors.toList());

        List<String> columns = args.stream().map(s -> decodeQueryToken(s[0])).collect(Collectors.toList());

        List<Object> values = args.stream().map(s -> decodeQueryToken(s[1])).collect(Collectors.toList());

        String quote = getQuote(sourceName);

        String condition = buildQueryConditionPart(columns, quote);

        return new MysqlDataPointer(source, buildSelectAllQuery(table, condition), buildSelectDoubleColumnQuery(table, condition, quote), values, new Transformation(transformations),uriString);
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

    private String getQuote(String sourceName){

        if (!quotes.containsKey(sourceName)){

            try (Connection connection = sources.get(sourceName).getConnection()) {

                quotes.put(sourceName,connection.getMetaData().getIdentifierQuoteString());

            } catch (SQLException e) {

                throw new RuntimeException(e);

            }

        }

        return quotes.get(sourceName);

    }

}

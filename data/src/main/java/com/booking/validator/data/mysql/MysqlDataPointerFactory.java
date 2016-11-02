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

    public DataPointer produce(String uriString) {

        URI uri = URI.create(uriString);

        String sourceName = uri.getHost();

        DataSource source = sources.get(sourceName);

        if (source == null) throw new RuntimeException("No such source");

        String table = uri.getPath().split("/")[1];

        List<String[]> args = Arrays.stream(uri.getQuery().split("&")).map(s->s.split("=")).collect(Collectors.toList());

        return new MysqlDataPointer(
                source,
                buildSelectQuery(
                        table,
                        args.stream().map(s -> decodeQueryToken(s[0])).collect(Collectors.toList()),
                        getQuote(sourceName)
                    ),
                args.stream().map(s -> decodeQueryToken(s[1])).collect(Collectors.toList())
            );

    }

    private String buildSelectQuery(String table, List<String> columns, String quote){
        return String.format("SELECT * FROM %s WHERE (%s) = (%s) LIMIT 2;",
                table,
                columns.stream().collect(Collectors.joining(quote + "," + quote, quote, quote)),
                Stream.generate( () -> "?" ).limit(columns.size()).collect(Collectors.joining(",")));
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

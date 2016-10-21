package com.booking.validator.data.mysql;

import com.booking.validator.data.Data;
import com.booking.validator.data.DataPointer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;


/**
 * Created by psalimov on 10/19/16.
 */
public class MysqlDataPointer implements DataPointer {

    private static final Logger LOGGER = LoggerFactory.getLogger(MysqlDataPointer.class);

    private final DataSource source;

    private final String query;
    private final Iterable<Object> args;

    public MysqlDataPointer(DataSource source, String query,Iterable<Object> args) {
        this.source = source;
        this.query = query;
        this.args = args;
    }

    @Override
    public Data resolve() {

        try (Connection connection = source.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)){

            int i = 1;
            for (Object arg : args) statement.setObject(i++,arg);

            ResultSet result = statement.executeQuery(); // will be closed on statement closing

            if (!result.next()) return null;

            if (!result.isLast()) throw new RuntimeException("An ambiguous data pointer to mysql source {}, the query {}");

            ResultSetMetaData meta = result.getMetaData();

            int columnCount = meta.getColumnCount();

            Map<String,String> data = new HashMap<>();

            for (i = 1; i <= columnCount; i++) data.put(meta.getColumnName(i),result.getString(i));

            return new Data(data);

        } catch (SQLException e) {

            LOGGER.error("Failed resolving a data pointer to mysql source {} using the query {} ", source, query, e);

            throw new RuntimeException(e);
        }

    }


}

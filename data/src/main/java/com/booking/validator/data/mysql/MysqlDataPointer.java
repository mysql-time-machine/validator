package com.booking.validator.data.mysql;

import com.booking.validator.data.Data;
import com.booking.validator.data.DataPointer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;


/**
 * Created by psalimov on 10/19/16.
 */
public class MysqlDataPointer implements DataPointer {

    public static class Cell{
        private final String type;
        private final Object value;

        public Cell(String type, Object value) {
            this.type = type;
            this.value = value;
        }

        public String getType() {
            return type;
        }

        public Object getValue() {
            return value;
        }
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(MysqlDataPointer.class);

    private final DataSource source;

    private final String query;
    private final Iterable<Object> args;
    private final Transformation transformation;

    public MysqlDataPointer(DataSource source, String query, Iterable<Object> args, Transformation transformation) {
        this.source = source;
        this.query = query;
        this.args = args;
        this.transformation = transformation;
    }

    @Override
    public Data resolve() {

        try (Connection connection = source.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)){

            int i = 1;
            for (Object arg : args) statement.setObject(i++,arg);

            ResultSet rows = statement.executeQuery(); // will be closed on statement closing

            if (!rows.next()) return null;

            if (!rows.isLast()) throw new RuntimeException("An ambiguous data pointer to mysql source {}, the query {}");

            ResultSetMetaData meta = rows.getMetaData();

            int columnCount = meta.getColumnCount();

            Map<String, Cell> cells = new HashMap<>();

            for (i = 1; i <= columnCount; i++) cells.put(meta.getColumnName(i),new Cell(meta.getColumnTypeName(i),rows.getObject(i)));

            Map<String,String> transformedCells = transformation.transform(cells);

            return transformedCells == null ? null : new Data(transformedCells);

        } catch (SQLException e) {

            LOGGER.error("Failed resolving a data pointer to mysql source {} using the query {} ", source, query, e);

            throw new RuntimeException(e);
        }

    }


}

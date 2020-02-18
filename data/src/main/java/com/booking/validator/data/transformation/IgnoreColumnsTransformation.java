package com.booking.validator.data.transformation;

import com.booking.validator.data.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by dbatheja on 10/02/20.
 */
public class IgnoreColumnsTransformation implements Transformation {
    static final Logger LOGGER = LoggerFactory.getLogger(IgnoreColumnsTransformation.class);
    private static final IgnoreColumnsTransformation instance = new IgnoreColumnsTransformation();
    private IgnoreColumnsTransformation(){}
    public static IgnoreColumnsTransformation getInstance(){
        return instance;
    }

    @Override
    public Data apply(Data data, Object options) {
        List<String> ignoreColumns;
        if(options instanceof ArrayList) {
            ignoreColumns = ((ArrayList<String>) options);
        } else if (options instanceof String) {
            ignoreColumns = new ArrayList<String>();
            ignoreColumns.add(options.toString());
        } else {
            ignoreColumns = new ArrayList<String>();
            LOGGER.warn("IgnoreColumns Transformation expects a list of Strings or a String");
        }
        Map<String, Object> row = data.getRow();
        for (String col: ignoreColumns) {
            row.remove(col);
        }
        return data;
    }

    @Override
    public Types getType() {
        return Types.IGNORE_COLUMNS;
    }
}

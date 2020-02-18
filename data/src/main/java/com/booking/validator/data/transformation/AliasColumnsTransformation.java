package com.booking.validator.data.transformation;

import com.booking.validator.data.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by dbatheja on 17/02/20.
 */
public class AliasColumnsTransformation implements Transformation {
    static final Logger LOGGER = LoggerFactory.getLogger(AliasColumnsTransformation.class);
    private static final AliasColumnsTransformation instance = new AliasColumnsTransformation();
    private AliasColumnsTransformation(){}
    public static AliasColumnsTransformation getInstance(){
        return instance;
    }

    @Override
    public Data apply(Data data, Object options) {
        HashMap<String, String> aliasColumns;
        if(options instanceof Map) {
            aliasColumns = ((HashMap<String, String>) options);
        } else {
            aliasColumns = new HashMap<String, String>();
            LOGGER.warn("AliasColumns Transformation expects a HashMap<String, String>");
        }
        Map<String, Object> row = data.getRow();
        for (String col: aliasColumns.keySet()) {
            row.put(aliasColumns.get(col), row.remove(col));
        }
        return data;
    }

    @Override
    public Types getType() {
        return Types.ALIAS_COLUMNS;
    }

}
package com.booking.validator.data.transformation;

import com.booking.validator.data.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by dbatheja on 10/02/20.
 */
public class KeepColumnsTransformation implements Transformation {
    static final Logger LOGGER = LoggerFactory.getLogger(IgnoreColumnsTransformation.class);
    private static final KeepColumnsTransformation instance = new KeepColumnsTransformation();
    private KeepColumnsTransformation(){}
    public static KeepColumnsTransformation getInstance(){
        return instance;
    }

    @Override
    public Data apply(Data data, Object options) {
        List<String> keepColumns;
        if(options instanceof ArrayList) {
            keepColumns = ((ArrayList<String>) options);
        } else if (options instanceof String) {
            keepColumns = new ArrayList<>();
            keepColumns.add(options.toString());
        } else {
            keepColumns = new ArrayList<>();
            LOGGER.warn("KeepColumns Transformation expects a list of Strings or a String");
        }
        Map<String, Object> row = data.getRow();
        row.entrySet()
           .removeIf(entry -> !keepColumns.contains(entry.getKey()));

        return data;
    }

    @Override
    public TransformationTypes getType() {
        return TransformationTypes.KEEP_COLUMNS;
    }
}

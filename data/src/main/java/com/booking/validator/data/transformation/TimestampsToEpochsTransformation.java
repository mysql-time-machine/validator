package com.booking.validator.data.transformation;

import com.booking.validator.data.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by dbatheja on 17/02/20.
 */
public class TimestampsToEpochsTransformation implements Transformation {
    static final Logger LOGGER = LoggerFactory.getLogger(TimestampsToEpochsTransformation.class);
    private TimestampsToEpochsTransformation() {}
    private static TimestampsToEpochsTransformation instance = new TimestampsToEpochsTransformation();
    public static TimestampsToEpochsTransformation getInstance() {
        return instance;
    }


    @Override
    public Data apply(Data data, Object options) {
        List<String> timestampColumns = new ArrayList<String>();
        boolean applyToAll;
        if(options instanceof List) {
            timestampColumns = (ArrayList<String>) options;
            applyToAll = false;
        } else if (options instanceof String) {
            timestampColumns.add((String) options);
            applyToAll = false;
        } else if (options instanceof Boolean && ((Boolean) options).booleanValue() == true){
            applyToAll = true;
        } else {
            applyToAll = false;
            LOGGER.warn("TimestampsToEpochs Transformation expects a List of timestamp columns or a single column name or a boolean to apply this transformation to all columns");
        }

        Map<String, Object> row = data.getRow();
        if (applyToAll == true) {
            row.replaceAll((k, v) -> isTimestampColumn(v) ? timestampToEpoch(v): v);
        } else {
            List<String> finalTimestampColumns = timestampColumns;
            row.replaceAll((k, v) -> finalTimestampColumns.contains(k) && isTimestampColumn(v) ? timestampToEpoch(v): v);
        }
        return data;
    }

    public Object timestampToEpoch(Object rawValue) {
        if (rawValue != null){
            Timestamp t = (Timestamp) rawValue;
            String value = String.valueOf(t.getTime());
            return value;
        }
        return rawValue;
    }

    public boolean isTimestampColumn(Object object) {
        return true;
    }

    @Override
    public TransformationTypes getType() {
        return TransformationTypes.TIMESTAMPS_TO_EPOCHS;
    }

}

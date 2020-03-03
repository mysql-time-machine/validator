package com.booking.validator.task.extra;

import com.booking.validator.data.Data;
import com.fasterxml.jackson.annotation.JsonCreator;
import org.apache.htrace.fasterxml.jackson.annotation.JsonProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by dbatheja on 03/03/20.
 */
public class AcceptableRanges extends Extra {
    private Map<String, List<Double>> ranges;
    static final Logger LOGGER = LoggerFactory.getLogger(Data.Discrepancy.class);

    @JsonCreator
    public AcceptableRanges() {
        super(ExtraTypes.ACCEPTABLE_RANGE_DIFFERENCES.getValue());
        ranges = new HashMap<>();
    }

    public AcceptableRanges add(String key, double s, double e) {
        if (s>0 || e < s) {
            LOGGER.warn("Acceptable ranges: s should be a negative number\n TIP: target is checked from [source + s, source + e]");
            return this;
        }
        if (!ranges.containsKey(key)) {
            ranges.put(key, new ArrayList<Double>(){{
                add(s);
                add(e);
            }});
        } else {
            LOGGER.warn("key:"+key+" already added to acceptable ranges Map");
        }
        return this;
    }
    public AcceptableRanges add(String key, double s) {
        if (!ranges.containsKey(key)) {
            ranges.put(key, new ArrayList<Double>(){{
                add(-Math.abs(s));
                add(Math.abs(s));
            }});
        } else {
            LOGGER.warn("key:"+key+" already added to acceptable ranges Map");
        }
        return this;
    }

    @JsonProperty("ranges")
    public Map<String, List<Double>> getRanges() {
        return ranges;
    }

    public void setRanges(Map<String, List<Double>> ranges) {
        this.ranges = ranges;
    }
}


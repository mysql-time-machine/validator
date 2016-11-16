package com.booking.validator.service.protocol;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

/**
 * Created by psalimov on 9/5/16.
 */
public class ValidationTaskDescription {

    private String source;

    private String target;

    private String id;

    private String tag;

    @JsonProperty("source_transformation")
    private Map<String,Object> sourceTransformation;

    @JsonProperty("target_transformation")
    private Map<String,Object> targetTransformation;

    public ValidationTaskDescription(){}

    public ValidationTaskDescription(String tag, String source, String target) {

        this.tag = tag;

        this.source = source;

        this.target = target;

    }

    public String getSource() {
        return source;
    }

    public String getTarget() {
        return target;
    }

    public String getTag() { return tag; }

    public String getId() { return id; }

    public void setId(String id) { this.id = id; }

    public Map<String, Object> getTargetTransformation() {
        return targetTransformation;
    }

    public Map<String, Object> getSourceTransformation() {
        return sourceTransformation;
    }

}

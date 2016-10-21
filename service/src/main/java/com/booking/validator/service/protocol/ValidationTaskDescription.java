package com.booking.validator.service.protocol;

/**
 * Created by psalimov on 9/5/16.
 */
public class ValidationTaskDescription {

    private String source;

    private String target;

    private String id;

    private String tag;

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

}

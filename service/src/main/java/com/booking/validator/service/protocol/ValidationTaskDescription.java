package com.booking.validator.service.protocol;

import com.booking.validator.service.protocol.DataPointerDescription;

/**
 * Created by psalimov on 9/5/16.
 */
public class ValidationTaskDescription {

    private DataPointerDescription source;

    private DataPointerDescription target;

    private String id;

    private String tag;

    public ValidationTaskDescription(){}

    public ValidationTaskDescription(String tag, DataPointerDescription source, DataPointerDescription target) {

        this.tag = tag;

        this.source = source;

        this.target = target;

    }

    public DataPointerDescription getSource() {
        return source;
    }

    public DataPointerDescription getTarget() {
        return target;
    }

    public String getTag() { return tag; }

    public String getId() { return id; }

    public void setId(String id) { this.id = id; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ValidationTaskDescription that = (ValidationTaskDescription) o;

        if (!source.equals(that.source)) return false;
        if (!target.equals(that.target)) return false;
        return tag != null ? tag.equals(that.tag) : that.tag == null;

    }

    @Override
    public int hashCode() {
        int result = source.hashCode();
        result = 31 * result + target.hashCode();
        result = 31 * result + (tag != null ? tag.hashCode() : 0);
        return result;
    }
}

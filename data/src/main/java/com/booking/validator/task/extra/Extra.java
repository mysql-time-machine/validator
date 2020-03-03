package com.booking.validator.task.extra;
import com.fasterxml.jackson.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dbatheja on 03/03/20.
 */
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = AcceptableRanges.class, name = ExtraTypes.Constants.ACCEPTABLE_RANGE_DIFFERENCES),
        @JsonSubTypes.Type(value = NestedComparisons.class, name = ExtraTypes.Constants.CHECK_NESTED_FIELDS),
})
public abstract class Extra {

    private String type;

    @JsonCreator
    public Extra(@JsonProperty("type") String type) {
        this.type = type;
    }

    @JsonIgnore
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public static List<Extra> builder() {
        return new ArrayList<>();
    }
}
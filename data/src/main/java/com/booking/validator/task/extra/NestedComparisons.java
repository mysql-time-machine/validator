package com.booking.validator.task.extra;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by dbatheja on 03/03/20.
 */
public class NestedComparisons extends Extra {
    private boolean enabled;

    @JsonCreator
    public NestedComparisons(@JsonProperty("enabled") boolean enabled) {
        super(ExtraTypes.Constants.CHECK_NESTED_FIELDS);
        this.enabled = enabled;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}

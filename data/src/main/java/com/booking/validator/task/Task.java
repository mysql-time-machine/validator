package com.booking.validator.task;

import com.booking.validator.data.Data;

/**
 * Created by dbatheja on 20/02/20.
 */
public interface Task {
    TaskComparisonResult validate(Data sourceData, Data targetData);
    String toJson() throws RuntimeException;
}

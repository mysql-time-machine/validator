package com.booking.validator.task;

import com.booking.validator.data.Data;

public interface Task {
    TaskComparisonResult validate(Data sourceData, Data targetData);
}

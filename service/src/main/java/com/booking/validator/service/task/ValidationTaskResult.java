package com.booking.validator.service.task;

import com.booking.validator.data.Data;

/**
 * Created by psalimov on 9/7/16.
 */
public class ValidationTaskResult {

    private final Data.Discrepancy dicrepancy;
    private final ValidationTask task;
    private final Throwable error;

    public ValidationTaskResult(ValidationTask task, Data.Discrepancy discrepancy, Throwable error) {

        this.task = task;
        this.dicrepancy = discrepancy;
        this.error = error;

    }

    public String getId() { return task.getId(); }

    public String getTag() { return task.getTag(); }

    public Throwable getError() { return error; }

    public boolean isOk(){ return  error == null && !dicrepancy.hasDiscrepancy(); }

    public Data.Discrepancy getDicrepancy(){ return dicrepancy; }

    public ValidationTask getTask(){
        return task;
    }
}

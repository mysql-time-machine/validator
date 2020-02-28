package com.booking.validator.task;

import com.booking.validator.data.Data;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Created by dbatheja on 20/02/20.
 */
public class TaskComparisonResult {

    @JsonProperty("discrepancy")
    private String discrepancyString;

    @JsonProperty("task")
    private Task task;

    @JsonIgnore
    private Data.Discrepancy discrepancy;

    @JsonIgnore
    private Throwable error = null;

    @JsonIgnore
    final private ObjectMapper mapper = new ObjectMapper();

    public TaskComparisonResult(Task task, Data.Discrepancy discrepancy, Throwable error) {
        this.task = task;
        this.discrepancy = discrepancy;
        this.error = error;
        this.discrepancyString = discrepancy != null ? discrepancy.toString() : "";
    }

    public TaskComparisonResult() {}


    public Throwable getError() { return error; }

    public boolean isOk(){ return  error == null && !discrepancy.hasDiscrepancy(); }

    public Data.Discrepancy getDiscrepancy(){ return discrepancy; }

    public Task getTask(){
        return task;
    }

    public void setDiscrepancyString(String discrepancyString) {
        this.discrepancyString = discrepancyString;
    }

    public void setDiscrepancy(Data.Discrepancy discrepancy) {
        this.discrepancy = discrepancy;
    }

    public void setTask(Task task) {
        this.task = task;
    }

    public String toJson() throws RuntimeException {
        try {
            String result = mapper.writeValueAsString(this);
            return result;
        } catch (Exception e) {
            throw new RuntimeException("Failed to serialize comparison object to JSON string", e);
        }
    }
}

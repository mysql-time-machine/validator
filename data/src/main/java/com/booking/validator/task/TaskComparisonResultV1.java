package com.booking.validator.task;

import com.booking.validator.data.Data;
import org.apache.htrace.fasterxml.jackson.annotation.JsonProperty;

public class TaskComparisonResultV1 {

        @JsonProperty("discrepancy")
        private final String discrepancyString;

        private final Data.Discrepancy discrepancy;

        @JsonProperty("task")
        private final Task task;

        private final Throwable error;

        public TaskComparisonResultV1(Task task, Data.Discrepancy discrepancy, Throwable error) {
            this.task = task;
            this.discrepancy = discrepancy;
            this.error = error;
            this.discrepancyString = discrepancy.toString();
        }


        public Throwable getError() { return error; }

        public boolean isOk(){ return  error == null && !discrepancy.hasDiscrepancy(); }

        public Data.Discrepancy getDiscrepancy(){ return discrepancy; }

        public Task getTask(){
            return task;
        }
}

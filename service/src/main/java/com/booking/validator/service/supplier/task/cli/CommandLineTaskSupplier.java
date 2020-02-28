package com.booking.validator.service.supplier.task.cli;

import com.booking.validator.task.Task;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.function.Supplier;

/**
 * Created by psalimov on 9/21/16.
 */
public class CommandLineTaskSupplier implements Supplier<Task> {

    private ObjectMapper mapper = new ObjectMapper();

    @Override
    public synchronized Task get() {

        try {
            String input = null;
            while (input == null || input.equals("")) {
                input = System.console().readLine();
            }
            return mapper.readValue( input , Task.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}

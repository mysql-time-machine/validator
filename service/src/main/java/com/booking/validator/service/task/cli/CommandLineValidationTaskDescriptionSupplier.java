package com.booking.validator.service.task.cli;

import com.booking.validator.service.protocol.ValidationTaskDescription;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;
import java.util.function.Supplier;

/**
 * Created by psalimov on 9/21/16.
 */
public class CommandLineValidationTaskDescriptionSupplier implements Supplier<ValidationTaskDescription> {

    private ObjectMapper mapper = new ObjectMapper();

    @Override
    public synchronized ValidationTaskDescription get() {

        try {

            String input = System.console().readLine();

            return mapper.readValue( input , ValidationTaskDescription.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}

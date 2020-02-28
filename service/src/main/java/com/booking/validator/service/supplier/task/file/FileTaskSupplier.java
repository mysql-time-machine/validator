package com.booking.validator.service.supplier.task.file;

import com.booking.validator.task.Task;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map;
import java.util.Scanner;
import java.util.function.Supplier;

/**
 * Created by dbatheja on 28/02/20.
 */
public class FileTaskSupplier implements Supplier<Task>{
    private static String FILE_PATH_PROPERTY = "path";
    Scanner scanner;
    public FileTaskSupplier(Map<String, String> config) {
        try {
            scanner = new Scanner(new File(config.get(FILE_PATH_PROPERTY)));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
    private ObjectMapper mapper = new ObjectMapper();
    @Override
    public Task get() {
        if (scanner.hasNext()) {
            String line = scanner.nextLine();
            try {
                return mapper.readValue( line , Task.class);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}

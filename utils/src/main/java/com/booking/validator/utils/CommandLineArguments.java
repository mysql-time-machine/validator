package com.booking.validator.utils;

/**
 * Created by psalimov on 10/3/16.
 */
public class CommandLineArguments {

    private String configurationPath;
    private Boolean useHbase;

    public CommandLineArguments(String... args){
        useHbase = false;
        for (int i=0; i<args.length; i++){
            if ("--config-file".equals(args[i])){
                configurationPath = args[++i];
            } else if ("--use-hbase".equals(args[i]) || "--hbase".equals(args[i])) {
                useHbase = true;
            }
        }

        if (configurationPath == null || configurationPath.isEmpty()) throw new RuntimeException("--config-file path is not provided");

    }

    public String getConfigurationPath() {

        return configurationPath;

    }

    public Boolean getUseHbase() {

        return useHbase;
    }
}

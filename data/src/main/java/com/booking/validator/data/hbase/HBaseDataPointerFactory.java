package com.booking.validator.data.hbase;


import com.booking.validator.data.DataPointer;
import com.booking.validator.data.DataPointerFactory;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;
import org.apache.hadoop.hbase.util.Bytes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by psalimov on 9/13/16.
 */
public class HBaseDataPointerFactory implements DataPointerFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(HBaseDataPointerFactory.class);

    private static final String HBASE = "hbase";

    private final Map<String, Connection> clusters;

    public static HBaseDataPointerFactory build(Map<String,Configuration> clusterConfigurations){

        Map<String, Connection> clusters = new HashMap<>();

        for ( Map.Entry<String,Configuration> clusterAndConfig : clusterConfigurations.entrySet() ){

            try {

                Connection connection = ConnectionFactory.createConnection( clusterAndConfig.getValue() );

                clusters.put( clusterAndConfig.getKey(), connection );

            } catch (IOException e) {

                LOGGER.error("Failed to create hbase connection", e);

            }

        }

        return new HBaseDataPointerFactory( clusters );

    }

    public HBaseDataPointerFactory(Map<String, Connection> clusters){
        this.clusters = clusters;
    }

    @Override
    public DataPointer produce(String uriString) throws InvalidDataPointerDescription {

        URI uri = URI.create(uriString);

        String sourceName = uri.getHost();

        Connection connection = clusters.get(sourceName);

        if (connection == null) throw new RuntimeException("No such source");

        String table = uri.getPath().split("/")[1];

        List<String[]> args = Arrays.stream(uri.getQuery().split("&")).map(s->s.split("=")).collect(Collectors.toList());

        String row = null;
        String cf = null;

        for (String[] arg : args){

            if ("row".equals(arg[0])) row = arg[1];
            if ("cf".equals(arg[0])) cf = arg[1];

        }

        if (row == null) throw new RuntimeException("No row given");
        if (cf == null) throw new RuntimeException("No cf given");


        return new HbaseDataPointer(connection, table, Bytes.toBytes(row), Bytes.toBytes(cf));

    }

}

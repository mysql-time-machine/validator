package com.booking.validator.connectors;

import com.booking.validator.data.Data;
import com.booking.validator.data.source.DataSource;
import com.booking.validator.data.transformation.TransformationFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by dbatheja on 12/02/20.
 *
 * This is used to have a single client connection per Data Source from Validator Config
 */
public class ActiveDataSourceConnections {
    private Map<String, DataSourceConnection> connections;
    private static final ActiveDataSourceConnections instance = new ActiveDataSourceConnections();
    private ActiveDataSourceConnections(){
        connections = new HashMap<>();
    }
    public static ActiveDataSourceConnections getInstance() { return instance; }

    public void add(String name, String type, Map<String, String> configuration) {
        DataSourceConnection conn = DataSourceConnectionFactory.initConnection(type, configuration);
        if (connections.containsKey(name)){
            throw new RuntimeException("DataSource name:" + name + " already exists in active connections.");
        } else if (conn == null) {
            throw new RuntimeException("DataSource connection couldn't be established:" + name + type + configuration.toString());
        } else {
            connections.put(name, conn);
        }
    }

    public DataSourceConnection get(String name) {
        if (connections.containsKey(name)) {
            return connections.get(name);
        } else {
            throw new RuntimeException("DataSource connection not found for name:" + name);
        }
    }

    public Data query(DataSource source) {
        DataSourceConnection conn = get(source.getName());
        return TransformationFactory.applyTransformations(conn.query(source.getOptions()), source.getOptions().getTransformations());
    }

    public void closeAll() {
        for(DataSourceConnection conn : connections.values()) { conn.close(); }
    }

    public Map<String, DataSourceConnection> getConnections() {
        return connections;
    }
}

package com.booking.validator.connectors.hbase;

import com.booking.validator.connectors.DataSourceConnection;
import com.booking.validator.data.Data;
import com.booking.validator.data.source.DataSourceQueryOptions;
import com.booking.validator.data.source.hbase.HbaseQueryOptions;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.util.Bytes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.*;


/**
 * Created by dbatheja on 20/02/20.
 */
public class HbaseDataSourceConnection implements DataSourceConnection {
    static final Logger LOGGER = LoggerFactory.getLogger(HbaseDataSourceConnection.class);

    private Connection connection;
    protected String name = "hbase";
    public HbaseDataSourceConnection(Map<String, String> config) {
        initConnection(config);
    }

    private void initConnection(Map<String, String> config) throws RuntimeException {
        Configuration configuration = HBaseConfiguration.create();
        config.forEach(configuration::set);
        try {
            connection = ConnectionFactory.createConnection(configuration);
        } catch (IOException e) {
            throw new RuntimeException("Failed to create " + this.name + " connection", e);
        }
    }

    @Override
    public Data query(DataSourceQueryOptions options) {
        HbaseQueryOptions queryOptions = (HbaseQueryOptions) options;
        String table = queryOptions.getTableName();
        String row = queryOptions.getRow();
        String cf = queryOptions.getColumnFamily();
        byte[] rowBytes = Bytes.toBytesBinary(row);
        byte[] columnFamilyBytes = Bytes.toBytes(cf);
        return resolve(table, rowBytes, columnFamilyBytes);
    }

    private Data resolve(String tableName, byte[] row, byte[] cf) {
        try ( Table table = connection.getTable(TableName.valueOf(tableName) ) ){
            Get get = new Get( row );
            Result fullRow = table.get(get);
            if (fullRow.isEmpty()) return null;
            NavigableMap<byte[],byte[]> familyMap = fullRow.getFamilyMap( cf );
            Map<String,Object> data = new HashMap<>();
            for (Map.Entry<byte[],byte[]> entry : familyMap.entrySet()){
                String column = Bytes.toString( entry.getKey() );
                data.put(column, Bytes.toString( entry.getValue() ));
            }
            return new Data( data );
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void close() {
        try {
            connection.close();
        } catch (IOException e) {
            LOGGER.error("Error while closing client connection: " + Arrays.toString(e.getStackTrace()));
        }
    }
}

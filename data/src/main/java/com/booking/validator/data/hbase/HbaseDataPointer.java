package com.booking.validator.data.hbase;

import com.booking.validator.data.Data;
import com.booking.validator.data.DataPointer;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.util.Bytes;

import java.io.IOException;
import java.util.NavigableMap;
import java.util.stream.Collectors;

/**
 * Created by psalimov on 10/21/16.
 */
public class HbaseDataPointer implements DataPointer {

    private final Connection connection;
    private final String tableName;
    private final byte[] row;
    private final byte[] family;

    public HbaseDataPointer(Connection connection, String tableName, byte[] row, byte[] family) {
        this.connection = connection;
        this.tableName = tableName;
        this.row = row;
        this.family = family;
    }

    @Override
    public Data resolve() {
        try ( Table table = connection.getTable(TableName.valueOf(tableName) ) ){

            Get get = new Get( row );

            Result result = table.get(get);

            if (result.isEmpty()) return null;

            // TODO: allow the key to specify the version (time)

            if (family != null){

                NavigableMap<byte[],byte[]> familyMap = result.getFamilyMap( family );

                return new Data( familyMap.entrySet().stream().collect(
                        Collectors.toMap(entry -> Bytes.toString( entry.getKey() ), entry -> Bytes.toString( entry.getValue() ) )
                    ) );

            } else {

                throw new UnsupportedOperationException("HBase key does not specify the column family");

            }

        } catch (IOException e) {

            throw new RuntimeException(e);

        }
    }



}

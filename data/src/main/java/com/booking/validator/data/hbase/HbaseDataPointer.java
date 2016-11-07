package com.booking.validator.data.hbase;

import com.booking.validator.data.Data;
import com.booking.validator.data.DataPointer;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.Table;

import java.io.IOException;

/**
 * Created by psalimov on 10/21/16.
 */
public class HbaseDataPointer implements DataPointer {

    private final Connection connection;
    private final String tableName;
    private final byte[] row;
    private final byte[] family;
    private final Transformation transformation;

    public HbaseDataPointer(Connection connection, String tableName, byte[] row, byte[] family, Transformation transformation) {
        this.connection = connection;
        this.tableName = tableName;
        this.row = row;
        this.family = family;
        this.transformation = transformation;
    }

    @Override
    public Data resolve() {

        try ( Table table = connection.getTable(TableName.valueOf(tableName) ) ){

            Get get = new Get( row );

            Result fullRow = table.get(get);

            if (fullRow.isEmpty()) return null;

            // TODO: allow the key to specify the version (time)

            return transformation.transform( fullRow.getFamilyMap( family ) );

        } catch (IOException e) {

            throw new RuntimeException(e);

        }

    }



}

package com.booking.validator.connectors;

import com.booking.validator.connectors.bigtable.BigtableDataSourceConnection;
import com.booking.validator.connectors.hbase.HbaseDataSourceConnection;
import com.booking.validator.data.source.Types;
import com.booking.validator.connectors.constant.ConstantDataSourceConnection;
import com.booking.validator.connectors.mysql.MysqlDataSourceConnection;

import java.util.Map;

/**
 * Created by dbatheja on 10/02/20.
 *
 * This is defined in the Task
 */
public class DataSourceConnectionFactory {
    static DataSourceConnection initConnection(String type, Map<String, String> configuration){
        switch(Types.fromString(type)){
            case CONSTANT:
                return new ConstantDataSourceConnection(configuration);
            case MYSQL:
                return new MysqlDataSourceConnection(configuration);
            case HBASE:
                return new HbaseDataSourceConnection(configuration);
            case BIGTABLE:
                return new BigtableDataSourceConnection(configuration);
            default:
                return null;
        }
    }
}

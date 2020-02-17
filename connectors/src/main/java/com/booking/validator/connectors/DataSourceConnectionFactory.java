package com.booking.validator.connectors;

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
            default:
                return null;
        }
    }
}

package com.booking.validator.data.source;

import com.booking.validator.data.source.mysql.MysqlDataSourceConnection;

import java.util.Map;

/**
 * Created by dbatheja on 10/02/20.
 *
 * This is defined in the Task
 */
public class DataSourceConnectionFactory {
    static DataSourceConnection initConnection(String type, Map<String, String> configuration){
        switch(Types.fromString(type)){
            case MYSQL:
                return new MysqlDataSourceConnection(configuration);
            default:
                return null;
        }
    }
}

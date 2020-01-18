package com.xucheng.fastmysql.api.exception;

import com.xucheng.fastmysql.api.config.FastMysqlConfig;

public class FastMysqlException extends RuntimeException{
    public FastMysqlException(String message){
        super(message);
    }

    public FastMysqlException(Throwable throwable){
        super(throwable);
    }
}

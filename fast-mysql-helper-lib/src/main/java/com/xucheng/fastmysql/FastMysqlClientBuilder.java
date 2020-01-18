package com.xucheng.fastmysql;

import com.xucheng.fastmysql.api.config.FastMysqlConfig;
import com.xucheng.fastmysql.impl.DefaultFastMysqlClient;

import javax.sql.DataSource;

public class FastMysqlClientBuilder {

    private FastMysqlConfig config = new FastMysqlConfig();

    public FastMysqlClientBuilder dataSource(DataSource dataSource){
        config.setDataSource(dataSource);
        return this;
    }

    public FastMysqlClientBuilder registerEntity(Class<?> entityClass){
        config.registerEntityDefinition(entityClass);
        return this;
    }

    public FastMysqlClientBuilder showSQL(boolean isShow){
        config.setShowSQL(isShow);
        return this;
    }
    public FastMysqlClientBuilder batchCount(int batchCount){
        config.setBatchCount(batchCount);
        return this;
    }
    public FastMysqlClientBuilder batchInterval(int batchInterval){
        config.setBatchInterval(batchInterval);
        return this;
    }

    public FastMysqlClientBuilder enableTransferQuotes(boolean isTransfer){
        config.transferQuotes(isTransfer);
        return this;
    }

    public FastMysqlClient build(){
        return new DefaultFastMysqlClient(config);
    }
}

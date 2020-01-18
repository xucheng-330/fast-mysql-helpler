package com.xucheng.fastmysql.api.config;

import com.xucheng.fastmysql.api.config.impl.DefaultAnnotationEntityDefinition;
import com.xucheng.fastmysql.api.config.impl.typeconvert.*;
import com.xucheng.fastmysql.api.config.stat.QpsStat;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class FastMysqlConfig {

    private DataSource dataSource;
    private Map<Class,EntityDefinition> entityDefinitionMap = new HashMap<>();
    private Map<Class,TypeCovert> typeCovertMap = new HashMap<>();

    private int batchCount = 100;
    private int batchInterval = 3;
    private QpsStat qpsStat = new QpsStat();
    private boolean showSQL = false;

    public void transferQuotes(boolean isTransfer){
        if (isTransfer){
            this.typeCovertMap.put(String.class,new StringTransferQuotesTypeCovert());
        }else {
            this.typeCovertMap.put(String.class,new StringTypeCovert());
        }
    }

    public boolean isShowSQL() {
        return showSQL;
    }

    public FastMysqlConfig setShowSQL(boolean showSQL) {
        this.showSQL = showSQL;
        return this;
    }

    public int getBatchCount() {
        return batchCount;
    }

    public FastMysqlConfig setBatchCount(int batchCount) {
        this.batchCount = batchCount;
        return this;
    }

    public int getBatchInterval() {
        return batchInterval;
    }

    public FastMysqlConfig setBatchInterval(int batchInterval) {
        this.batchInterval = batchInterval;
        return this;
    }

    public QpsStat getQpsStat() {
        return qpsStat;
    }

    public FastMysqlConfig setQpsStat(QpsStat qpsStat) {
        this.qpsStat = qpsStat;
        return this;
    }

    {
        NumberTypeCovert ntc = new NumberTypeCovert();
        typeCovertMap.put(int.class,ntc);
        typeCovertMap.put(byte.class,ntc);
        typeCovertMap.put(short.class,ntc);
        typeCovertMap.put(long.class,ntc);

        typeCovertMap.put(Byte.class,ntc);
        typeCovertMap.put(Short.class,ntc);
        typeCovertMap.put(Integer.class,ntc);
        typeCovertMap.put(Long.class,ntc);

        typeCovertMap.put(float.class,ntc);
        typeCovertMap.put(Float.class,ntc);
        typeCovertMap.put(double.class,ntc);
        typeCovertMap.put(Double.class,ntc);
        typeCovertMap.put(BigDecimal.class,ntc);

        typeCovertMap.put(boolean.class,ntc);
        typeCovertMap.put(Boolean.class,ntc);

        typeCovertMap.put(null,new NullCovert());
        typeCovertMap.put(String.class,new StringTypeCovert());
        typeCovertMap.put(Date.class,new DateTypeCovert());
    }

    public Map<Class,EntityDefinition> getEntityDefinitionMap(){
        return this.entityDefinitionMap;
    }

    public void registerEntityDefinition(Class entityClass){
        EntityDefinition definition = new DefaultAnnotationEntityDefinition(entityClass);
        this.entityDefinitionMap.put(entityClass,definition);
    }
    public void registerEntityDefinition(EntityDefinition entityDefinition){
        this.entityDefinitionMap.put(entityDefinition.getType(),entityDefinition);
    }

    public void typeCovert(Class c,TypeCovert tc) {
        this.typeCovertMap.put(c,tc);
    }

    public void assertValidateRequest(Object request){
        EntityDefinition definition = entityDefinitionMap.get(request.getClass());
        if(definition==null){
            throw  new RuntimeException("该实体没有定义！！！");
        }
    }

    public void setDataSource(DataSource dataSource){
        this.dataSource = dataSource;
    }

    public DataSource getDataSource(){
        return dataSource;
    }

    public String getInsertSQLValue(Object obj){
        if (obj==null){
            return "null";
        }
        return this.typeCovertMap.get(obj.getClass()).java2DMLSQLString(obj);
    }



}

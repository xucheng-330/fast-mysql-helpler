package com.xucheng.fastmysql.impl.sql;

import com.xucheng.fastmysql.api.config.EntityDefinition;
import com.xucheng.fastmysql.api.config.FastMysqlConfig;

import java.util.*;

public class BatchSQL {

    private final FastMysqlConfig config;

    private Map<EntityDefinition, List<Object[]>> batchSQLSource = new HashMap<>();

    public BatchSQL(FastMysqlConfig config){
        this.config = config;
    }

    public void addRequest(Object request){
        EntityDefinition definition = config.getEntityDefinitionMap().get(request.getClass());
        if(definition==null){
            throw  new RuntimeException("该实体没有定义！！！");
        }
        List<Object[]> strings = batchSQLSource.computeIfAbsent(definition, k -> new ArrayList<Object[]>());
        Object[] columnsValue = definition.getColumnsValue(request);
        strings.add(columnsValue);
    }

    public Iterator<String> iterator(){
        return new Iterator<String>() {
            Iterator<EntityDefinition> iter = batchSQLSource.keySet().iterator();
            @Override
            public boolean hasNext() {
                return iter.hasNext();
            }

            @Override
            public String next() {
                StringBuilder sql = new StringBuilder();
                EntityDefinition batchEntity = iter.next();
                List<Object[]> colValues = batchSQLSource.get(batchEntity);
                appendSQL(sql,batchEntity,colValues);
                //sql.append("; ");
                return sql.toString();
            }
        };
    }

    public String batchSQL(){
        StringBuilder sql = new StringBuilder();
        Iterable<EntityDefinition> batchEntity = this.batchSQLSource.keySet();
        for (EntityDefinition definition : batchEntity) {
            List<Object[]> colValues = this.batchSQLSource.get(definition);
            this.appendSQL(sql,definition,colValues);
            sql.append("; ");
        }
        return sql.toString();
    }

    private void appendSQL(StringBuilder sql,EntityDefinition definition, List<Object[]> colValues) {
        appendHeadSQL(sql,definition);
        appendValuesSQL(sql,colValues);
    }

    private void appendValuesSQL(StringBuilder sql, List<Object[]> colValues) {
        sql.append("values ");
        for (int i = 0; i < colValues.size(); i++) {
            Object[] cols = colValues.get(i);
            if (i>0){
                sql.append(",");
            }
            sql.append("(");
            for (int j = 0; j < cols.length; j++) {
                if (j>0){
                    sql.append(",");
                }
                sql.append(config.getInsertSQLValue(cols[j]));
            }
            sql.append(")");
        }
    }

    private void appendHeadSQL(StringBuilder sql, EntityDefinition definition) {
        sql.append("insert into ").
                append("`").
                append(definition.getTableName()).
                append("`").
                append("(");

        String[] columnsName = definition.getColumnsName();
        for (int i = 0; i < columnsName.length; i++) {
            String col = columnsName[i];
            if(i>0){
                sql.append(",");
            }
            sql.append("`").append(col).append("`");
        }
        sql.append(") ");
    }


}

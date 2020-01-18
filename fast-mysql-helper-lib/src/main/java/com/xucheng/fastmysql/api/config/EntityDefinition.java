package com.xucheng.fastmysql.api.config;

public interface EntityDefinition<T> {
    public Class<T> getType();
    public String getTableName();
    public String[] getColumnsName();
    public ColumnDefinition[] getColumns();
    public Object[] getColumnsValue(T t);
}

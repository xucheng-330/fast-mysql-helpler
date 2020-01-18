package com.xucheng.fastmysql.api.config;

public abstract class AbstractEntityDefinition<T> implements EntityDefinition<T> {

    private final Class<T> type;
    private String[] columnsName;

    public AbstractEntityDefinition(Class<T> type) {
        this.type = type;
    }

    @Override
    public Class<T> getType() {
        return type;
    }

    @Override
    public String[] getColumnsName() {
        if (columnsName!=null) return columnsName;

        ColumnDefinition[] columns = this.getColumns();
        String[] columnsName = new String[columns.length];
        for (int i = 0; i < columns.length; i++) {
            columnsName[i] = columns[i].getName();
        }
        this.columnsName = columnsName;

        return columnsName;
    }


    public abstract String getTableName();
    public abstract ColumnDefinition[] getColumns();
    public abstract Object[] getColumnsValue(T t);
}

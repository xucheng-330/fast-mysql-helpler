package com.xucheng.fastmysql.api.config.impl;

import com.xucheng.fastmysql.api.config.AbstractEntityDefinition;
import com.xucheng.fastmysql.api.config.ColumnDefinition;
import com.xucheng.fastmysql.api.config.EntityDefinition;
import com.xucheng.fastmysql.api.config.annotation.AnnotationUtils;

import java.lang.reflect.Field;

public class DefaultAnnotationEntityDefinition<T> extends AbstractEntityDefinition<T> {

    private final Field[] columnsField;
    private final String tableName;
    private final ColumnDefinition[] columnsDefs;


    public DefaultAnnotationEntityDefinition(Class<T> type) {
        super(type);
        this.tableName = AnnotationUtils.getTableName(type);
        this.columnsField = AnnotationUtils.getColumnFields(type);

        this.columnsDefs = new ColumnDefinition[this.columnsField.length];
        for (int i = 0; i < columnsField.length; i++) {
            Field f = columnsField[i];
            this.columnsDefs[i] = AnnotationUtils.getFieldAnnotation(f);
        }
    }

    @Override
    public String getTableName() {
        return tableName;
    }

    @Override
    public ColumnDefinition[] getColumns() {
        return columnsDefs;
    }

    @Override
    public Object[] getColumnsValue(T t) {
        Object[] colValues = new Object[columnsField.length];
        for (int i = 0; i < columnsField.length; i++) {
            Field f = columnsField[i];
            f.setAccessible(true);
            try {
                Object o = f.get(t);
                if (o!=null){
                    colValues[i] = o;
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return colValues;
    }
}

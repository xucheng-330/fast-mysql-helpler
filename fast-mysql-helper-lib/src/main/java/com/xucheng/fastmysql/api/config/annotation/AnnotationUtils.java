package com.xucheng.fastmysql.api.config.annotation;

import com.xucheng.fastmysql.api.config.ColumnDefinition;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class AnnotationUtils {

    public static String getTableName(Class type){
        Table table = (Table) type.getAnnotation(Table.class);
        if (table!=null&&table.value()!=null&&!"".equals(table.value())){
            return table.value();
        }
        String name = type.getSimpleName();
        return name;
    }

    public static ColumnDefinition getFieldAnnotation(Field field){
        String name = field.getName();
        Class<?> colType = field.getType();
        Integer length = null;
        Integer decimalLength = null;
        Column column = field.getAnnotation(Column.class);
        if (column!=null){
            if (!column.value().equals("")){
                name = column.value();
            }
            if(-1!=column.length()&&column.length()>0){
                length = column.length();
            }else {
                if (colType==String.class){
                    length = 512;
                }
            }
            if (colType==float.class||colType==double.class||colType== BigDecimal.class
                || colType==Float.class||colType==Double.class){
                decimalLength = column.decimalLength();
            }
        }


        ColumnDefinition columnDef = new ColumnDefinition(colType,name);
        return columnDef;
    }

    public static Field[] getColumnFields(Class type){
        Field[] declaredFields = type.getDeclaredFields();
        List<Field> list = new ArrayList<>(declaredFields.length);
        for (int i = 0; i < declaredFields.length; i++) {
            Field f = declaredFields[i];
            Ignore ignore = f.getAnnotation(Ignore.class);
            if (ignore!=null){
                continue;
            }
            ID id = f.getAnnotation(ID.class);
            if (id!=null && id.autoIncrement()){
                continue;
            }
            if (f.isSynthetic()){
                continue;
            }
            list.add(f);
        }
        return list.toArray(new Field[list.size()]);
    }

}

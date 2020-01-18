package com.xucheng.fastmysql.api.config.impl.typeconvert;

import com.xucheng.fastmysql.api.config.ColumnDefinition;
import com.xucheng.fastmysql.api.config.TypeCovert;

public class NumberTypeCovert implements TypeCovert {

    @Override
    public String java2DMLSQLString(Object obj) {
        if (obj==null){
            return "null";
        }
        return obj.toString();
    }

}

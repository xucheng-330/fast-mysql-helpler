package com.xucheng.fastmysql.api.config.impl.typeconvert;

import com.xucheng.fastmysql.api.config.TypeCovert;

public class StringTypeCovert implements TypeCovert {
    @Override
    public String java2DMLSQLString(Object t) {
        if (t==null){
            return "null";
        }
        return "'" + t.toString() + "'";
    }
}

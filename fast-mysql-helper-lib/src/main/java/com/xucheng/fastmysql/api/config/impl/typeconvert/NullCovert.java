package com.xucheng.fastmysql.api.config.impl.typeconvert;

import com.xucheng.fastmysql.api.config.TypeCovert;

public class NullCovert implements TypeCovert {

    @Override
    public String java2DMLSQLString(Object obj) {
        return "null";
    }

}

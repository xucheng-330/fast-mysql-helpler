package com.xucheng.fastmysql.api.config.impl.typeconvert;

import com.xucheng.fastmysql.api.config.TypeCovert;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateTypeCovert implements TypeCovert {

    @Override
    public String java2DMLSQLString(Object obj) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        if (obj==null){
            return "null";
        }
        return "'"+sdf.format((Date) obj)+"'";
    }

}

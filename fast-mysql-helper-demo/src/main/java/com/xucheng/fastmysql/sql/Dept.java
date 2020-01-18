package com.xucheng.fastmysql.sql;

import com.xucheng.fastmysql.api.config.annotation.ID;
import com.xucheng.fastmysql.api.config.annotation.Table;

@Table("tbl_dept")
public class Dept {
    @ID
    Long dept_id;
    String dept_name;
    Integer age;
    String desc;

    public Dept(String dept_name, Integer age, String desc) {
        this.dept_name = dept_name;
        this.age = age;
        this.desc = desc;
    }
}

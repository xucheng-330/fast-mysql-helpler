package com.xucheng.fastmysql.sql;

import com.xucheng.fastmysql.api.config.annotation.Column;
import com.xucheng.fastmysql.api.config.annotation.ID;
import com.xucheng.fastmysql.api.config.annotation.Table;

@Table("tbl_dept")//与数据库对应的表名
public class Dept {
    @ID //自增长ID才需要标注，手动指定ID无需标注
    Long dept_id;
    @Column("dept_name")//成员变量对应的数据库表字段名
    String name;
    Integer age;
    String desc;

    public Dept(String dept_name, Integer age, String desc) {
        this.name = dept_name;
        this.age = age;
        this.desc = desc;
    }
}

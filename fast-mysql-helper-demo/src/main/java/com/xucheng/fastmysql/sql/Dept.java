package com.xucheng.fastmysql.sql;

import com.xucheng.fastmysql.api.config.annotation.Column;
import com.xucheng.fastmysql.api.config.annotation.ID;
import com.xucheng.fastmysql.api.config.annotation.Table;

@Table("tbl_dept")//�����ݿ��Ӧ�ı���
public class Dept {
    @ID //������ID����Ҫ��ע���ֶ�ָ��ID�����ע
    Long dept_id;
    @Column("dept_name")//��Ա������Ӧ�����ݿ���ֶ���
    String name;
    Integer age;
    String desc;

    public Dept(String dept_name, Integer age, String desc) {
        this.name = dept_name;
        this.age = age;
        this.desc = desc;
    }
}

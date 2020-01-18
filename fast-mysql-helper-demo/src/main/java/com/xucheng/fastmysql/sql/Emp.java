package com.xucheng.fastmysql.sql;

import com.xucheng.fastmysql.api.config.annotation.ID;
import com.xucheng.fastmysql.api.config.annotation.Table;

@Table("tbl_emp")
public class Emp {

    @ID
    private Long emp_id;
    private String emp_name;
    private String gender;
    private String email;
    private  Long d_id;

    public Emp(Long emp_id, String emp_name, String gender, String email, Long d_id) {
        this.emp_id = emp_id;
        this.emp_name = emp_name;
        this.gender = gender;
        this.email = email;
        this.d_id = d_id;
    }

    public Long getEmp_id() {
        return emp_id;
    }

    public Emp setEmp_id(Long emp_id) {
        this.emp_id = emp_id;
        return this;
    }

    public String getEmp_name() {
        return emp_name;
    }

    public Emp setEmp_name(String emp_name) {
        this.emp_name = emp_name;
        return this;
    }

    public String getGender() {
        return gender;
    }

    public Emp setGender(String gender) {
        this.gender = gender;
        return this;
    }

    public String getEmail() {
        return email;
    }

    public Emp setEmail(String email) {
        this.email = email;
        return this;
    }

    public Long getD_id() {
        return d_id;
    }

    public Emp setD_id(Long d_id) {
        this.d_id = d_id;
        return this;
    }
}

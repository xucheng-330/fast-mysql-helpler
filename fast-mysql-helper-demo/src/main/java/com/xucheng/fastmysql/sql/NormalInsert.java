package com.xucheng.fastmysql.sql;

import com.alibaba.druid.sql.visitor.functions.Insert;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class NormalInsert {

    DataSource dataSource;

    public NormalInsert(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public boolean insert(Emp emp,Dept dept){
        try(Connection connection = dataSource.getConnection();
            Statement statement = connection.createStatement()) {

            String empSql = new StringBuilder("insert into `tbl_emp`(`emp_name`,`gender`,`email`,`d_id`) values ('")
                    .append(emp.getEmp_name()).append("','").append(emp.getGender())
                    .append("','").append(emp.getEmail()).append("',").append(emp.getD_id()).append(")").toString();

            String deptSql = new StringBuilder("insert into `tbl_dept`(`dept_name`,`age`,`desc`) values ('")
                    .append(dept.name).append("',").append(dept.age).append(",'")
                    .append(dept.desc).append("')").toString();

           // System.out.println(empSql);
            //System.out.println(deptSql);

            connection.setAutoCommit(false);
            statement.addBatch(empSql);
            statement.addBatch(deptSql);
            statement.executeBatch();
            connection.commit();
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}

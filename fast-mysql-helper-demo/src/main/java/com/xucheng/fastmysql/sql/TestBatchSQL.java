package com.xucheng.fastmysql.sql;

import com.xucheng.fastmysql.api.config.FastMysqlConfig;
import com.xucheng.fastmysql.impl.sql.BatchSQL;
import org.junit.Test;

public class TestBatchSQL {

    class Person {
        private Long id;
        private String name;
        private Integer age;

        public Person(Long id, String name, Integer age) {
            this.id = id;
            this.name = name;
            this.age = age;
        }
    }

    @Test
    public void testBatchSQL(){
        FastMysqlConfig config = new FastMysqlConfig();
        config.registerEntityDefinition(Person.class);

        BatchSQL batchSQL = new BatchSQL(config);

        batchSQL.addRequest(new Person(1L,"xucheng",34));
        batchSQL.addRequest(new Person(2L,"xucheng2",34));
        batchSQL.addRequest(new Person(3L,"xucheng3",34));

        String s = batchSQL.batchSQL();
        System.out.println(s);

    }
}

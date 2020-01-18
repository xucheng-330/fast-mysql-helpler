# What fast-mysql-helper

fast-mysql-helper is a High performance mysql insert data accelerator lib.

# Why Use?

MySQL数据库，插入性能受到IO的限制，通常在百级的TPS或者千级的TPS。

Fast_MySQL_Helper非常快，能达到万级的TPS，通过批量的方式，提升插入性能，提升50倍的性能！

高并发： 固态硬盘的笔记本达到6.5万TPS。
低延迟：200毫秒的延迟
轻量级：简单易用，依赖少



The insertion performance of MySQL database is limited by IO, usually in level 100 TPS or level 1000 TPS.

Fast MySQL helper is very fast and can reach 10000 level TPS. Through batch mode, it can improve insertion performance and 50 times performance!

High concurrency: the notebook of SSD reaches 65000 TPS.

Low latency: 200 ms latency

Lightweight: easy to use, less dependence

# How Use

1、执行建表语句

```mysql
-- Table structure for tbl_dept
-- ----------------------------
DROP TABLE IF EXISTS `tbl_dept`;
CREATE TABLE `tbl_dept` (
  `dept_id` int(11) NOT NULL AUTO_INCREMENT,
  `dept_name` varchar(255) DEFAULT NULL,
  `age` int(11) DEFAULT NULL,
  `desc` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`dept_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Table structure for tbl_emp
-- ----------------------------
DROP TABLE IF EXISTS `tbl_emp`;
CREATE TABLE `tbl_emp` (
  `emp_id` int(11) NOT NULL AUTO_INCREMENT,
  `emp_name` varchar(255) DEFAULT NULL,
  `gender` char(1) DEFAULT NULL,
  `email` varchar(255) DEFAULT NULL,
  `d_id` int(11) DEFAULT NULL,
  PRIMARY KEY (`emp_id`),
  KEY `fk_dept` (`d_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
```

2、准备实体类

```java
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
```

```java
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
```

3、配置创建FastMysqlClient对象

```java
//创建数据源对象，
DruidDataSource datasource = new DruidDataSource();
//注意参数characterEncoding=utf-8&useSSL=false&allowMultiQueries=true
datasource.setUrl("jdbc:mysql://localhost:3306/fast_mysql_demo?characterEncoding=utf-8&useSSL=false&allowMultiQueries=true");
    
datasource.setUsername("root");
datasource.setPassword("123456");
datasource.setDriverClassName("com.mysql.jdbc.Driver");

//实例化FastMysqlClient
FastMysqlClient client = new FastMysqlClientBuilder().
dataSource(datasource).//设置数据源
registerEntity(Emp.class).//注册实体类
registerEntity(Dept.class).
showSQL(false).//设置是否打印SQL
batchCount(100).//重点：每次批量插入的事务数
batchInterval(2).//重点：批次的间隔时间，影响响应延迟
enableTransferQuotes(false).//字符转译 '-->\' ，字符串中带有单引号需配置为true，影响性能
build();
```

4、提交插入事务

```java
Emp m1 = new Emp(null, "成哥" + i, "M", "xucheng@qq.com", 100l);
Dept dept = new Dept("技术部" ,145,"多好的一个技术部啊，为什么还有这么多想法呢。");

client.startMultiFastInsert() //开启一个插入事务
      .add(m1)//添加要插入的数据
      .add(dept)
      .asyncCallback(//注册异步回调函数，当事务完成以后，会回调。 当然也可以用同步的方式
           new AsyncCallback() {
              @Override
              public void callback(AsyncResultFuture future,
                                   boolean result, Throwable cause) {
                  if (result){//事务执行结果，true 代表成功，
                      //事务执行成功，逻辑
                  }else {
                      //执行失败，返回的异常信息
                      cause.printStackTrace();
                  }
              }
       })
      .commit()//提交事务到执行队列中去
      .get();//挂起线程，同步获取结果,建议用异步的方式，性能更高！

//插入单条数据
final AsyncResultFuture asyncResultFuture = client.fastInsert(m1);
//同步获取结果
final boolean b = asyncResultFuture.get();

//插入单条数据，异步获取结果
client.fastInsert(null, new AsyncCallback() {
    @Override
    public void callback(AsyncResultFuture future, 
                         boolean result, Throwable cause) {

    }
});
```


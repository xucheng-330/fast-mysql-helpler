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

//插入单条数据，异步获取结果, 异步的方式结合servlet3.0异步api获取最大性能
client.fastInsert(m1, new AsyncCallback() {
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
});
```

# 性能测试结果

执行fast-mysql-helper-demo中的FastMysqlClientTest测试用例

```java
package com.xucheng.fastmysql.sql;

import com.alibaba.druid.pool.DruidDataSource;
import com.xucheng.fastmysql.FastMysqlClient;
import com.xucheng.fastmysql.FastMysqlClientBuilder;
import com.xucheng.fastmysql.api.AsyncCallback;
import com.xucheng.fastmysql.api.AsyncResultFuture;
import com.xucheng.fastmysql.api.config.stat.QpsTaskInfo;
import org.junit.Test;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

public class FastMysqlClientTest {

    private AtomicLong count = new AtomicLong(0);

    //模拟10个线程提交事务
    private int threadCount = 10;
    //每个线程执行100000个事务
    private int perThreadTaskCount = 100000;
    //总计执行事务数 100万
    private int totalTaskCount = perThreadTaskCount*threadCount;

    @Test
    public void testFastMysqlClient() throws InterruptedException, IOException {

        //创建数据源对象，注意参数characterEncoding=utf-8&useSSL=false&allowMultiQueries=true
        DruidDataSource datasource = new DruidDataSource();
        datasource.setUrl("jdbc:mysql://localhost:3306/fast_mysql_demo?characterEncoding=utf-8&useSSL=false&allowMultiQueries=true");
        datasource.setUsername("root");
        datasource.setPassword("123456");
        datasource.setDriverClassName("com.mysql.jdbc.Driver");

        //实例化FastMysqlClient
        final FastMysqlClient client = new FastMysqlClientBuilder().
                dataSource(datasource).//设置数据源
                registerEntity(Emp.class).//注册实体类
                registerEntity(Dept.class).
                showSQL(false).//设置是否打印SQL
                batchCount(500).//重点：每次批量插入的事务数
                batchInterval(6).//重点：批次的间隔时间，影响响应延迟
                enableTransferQuotes(false).//字符转译 '-->\' ，字符串中带有单引号需配置为true，影响性能
                build();

        long globalStart = System.currentTimeMillis();
        for (int i = 0; i < threadCount; i++) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    for (int i = 0; i < perThreadTaskCount; i++) {
                        try {
                            Emp m1 = new Emp(null, "成哥" + i, "M", "xucheng@qq.com", 100l);
                            Dept dept = new Dept("技术部" ,145,"多好的一个技术部啊，为什么还有这么多想法呢。");

                            client.startMultiFastInsert().add(m1).
                                    add(dept).
                                    asyncCallback(new AsyncCallback() {
                                        @Override
                                        public void callback(AsyncResultFuture future, boolean result, Throwable cause) {
                                            if (result){
                                                count.incrementAndGet();
                                            }else {
                                                cause.printStackTrace();
                                            }
                                        }
                                    }).
                                    commit();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }).start();
        }

        //等待所有事务执行完毕
        while (!(totalTaskCount==count.intValue())){
            Thread.sleep(1000);
            System.out.println(count.longValue());
        }

        long globalEnd = System.currentTimeMillis();

        List<QpsTaskInfo> qpsTaskInfos = client.recentlyQPS();
        long start = 0;
        long end = 0;
        int taskCount = 0;

        for (QpsTaskInfo qpsTaskInfo : qpsTaskInfos) {
            if (start==0){
                start = qpsTaskInfo.start;
            }
            end = qpsTaskInfo.start+qpsTaskInfo.time;
            taskCount+= qpsTaskInfo.taskCount;
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss:SSS");
            System.out.println(sdf.format(qpsTaskInfo.start)+ " ====" + qpsTaskInfo.taskCount + "任务，用时" + qpsTaskInfo.time + "毫秒");
        }

        System.out.println("总计任务数：" + totalTaskCount + "， 共用时（毫秒）： " + (globalEnd-globalStart));
        System.out.println("最近"+qpsTaskInfos.size()+"批次用时(毫秒)： " +(end-start));
        System.out.println("平均每秒任务数:" + (taskCount*1000/(end-start)));


    }
}

```

部分测试结果：

```text
22:24:25:573 ====500任务，用时70毫秒
22:24:25:580 ====500任务，用时70毫秒
22:24:25:587 ====500任务，用时140毫秒
22:24:25:596 ====500任务，用时143毫秒
22:24:25:603 ====500任务，用时131毫秒
22:24:25:611 ====500任务，用时129毫秒
22:24:25:618 ====500任务，用时117毫秒
22:24:25:625 ====500任务，用时118毫秒
22:24:25:632 ====500任务，用时131毫秒
22:24:25:638 ====500任务，用时124毫秒
22:24:25:645 ====500任务，用时125毫秒
22:24:25:652 ====500任务，用时129毫秒
22:24:25:660 ====500任务，用时129毫秒
22:24:25:680 ====500任务，用时137毫秒
22:24:25:690 ====500任务，用时146毫秒
22:24:25:697 ====500任务，用时138毫秒
22:24:25:704 ====500任务，用时134毫秒
22:24:25:711 ====500任务，用时121毫秒
22:24:25:723 ====500任务，用时122毫秒
22:24:25:730 ====482任务，用时112毫秒
总计任务数：1000000， 共用时（毫秒）： 15022
最近2001批次用时(毫秒)： 14311
平均每秒任务数:69876
```

结论： 每批执行500个插入事务，共插入100万条数据，用时15秒，每秒插入事务数将近7万，平均延迟200毫秒！

注意：以下两个参数，对性能影响极大

batchCount(500).//重点：每次批量插入的事务数
batchInterval(6).//重点：批次的间隔时间，影响响应延迟



# 传统插入方式的性能测试结果

执行fast-mysql-helper-demo中的SlowMysqlClientTest

```java
public class SlowMysqlClientTest {

    private AtomicLong count = new AtomicLong(0);

    //20个线程一起提交事务
    private int threadCount = 20;
    //每个线程提交3000个事务
    private int perThreadTaskCount = 3000;
    //总计提交60000个事务
    private int totalTaskCount = perThreadTaskCount*threadCount;

    @Test
    public void testFastMysqlClient() throws InterruptedException, IOException {

        DruidDataSource datasource = new DruidDataSource();
        datasource.setUrl("jdbc:mysql://localhost:3306/ssm_crud?characterEncoding=utf-8&useSSL=false&allowMultiQueries=true");
        datasource.setUsername("root");
        datasource.setPassword("123456");
        datasource.setDriverClassName("com.mysql.jdbc.Driver");

        NormalInsert normalInsert = new NormalInsert(datasource);


        long globalStart = System.currentTimeMillis();
        for (int i = 0; i < threadCount; i++) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    long start = System.currentTimeMillis();
                    for (int i = 0; i < perThreadTaskCount; i++) {
                        try {
                            Emp m1 = new Emp(null, "成哥" + i, "M", "xucheng@qq.com", 100l);
                            Dept dept = new Dept("技术部" + i,120,"好一个技术部！！！！！");

                            final boolean insert = normalInsert.insert(m1, dept);
                            if (insert){
                                count.incrementAndGet();
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    long end = System.currentTimeMillis();
                    System.out.println("执行任务数： " + perThreadTaskCount + ", 用时（毫秒）：" + (end-start));
                }
            }).start();
        }

        while (!(totalTaskCount==count.intValue())){
            Thread.sleep(1000);
            System.out.println(count.longValue());
        }

        long globalEnd = System.currentTimeMillis();
        System.out.println("总计任务数：" + totalTaskCount + "， 共用时（毫秒）： " + (globalEnd-globalStart));
        System.out.println("每秒任务数: " + totalTaskCount * 1000/(globalEnd-globalStart));
    }
}

```

NormalInsert类代码

```java

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
```

运行测试用例执行结果：

```text
执行任务数： 3000, 用时（毫秒）：43701
执行任务数： 3000, 用时（毫秒）：43775
执行任务数： 3000, 用时（毫秒）：43828
执行任务数： 3000, 用时（毫秒）：44071
执行任务数： 3000, 用时（毫秒）：44092
执行任务数： 3000, 用时（毫秒）：44535
执行任务数： 3000, 用时（毫秒）：44724
执行任务数： 3000, 用时（毫秒）：45155

总计任务数：60000， 共用时（毫秒）： 46028
每秒任务数: 1303
```



结论：6万个事务，执行了46秒， 平次每个线程的用时44秒。每秒事务数1303



# 总结

使用fast-mysql-helper库加速：

每秒事务数 从 1303  提升到  69876 并发量提升了53倍！！！



传统方式： 平均每个事务需要14毫秒， 即单个事务延迟比较少。

fast-mysql-helper的方式：每批事务（500个事务）需要约200毫秒的延迟，故fast-mysql-helper是以牺牲单个事务请的响应时间，来换取批量插入的性能。 提升并发度。 200毫秒在大多数场景下是完成可以接受的，而且，你可以通过指定如下两个参数，来进行调整。

batchCount(500).//越小，延迟就越小,并发性能会减少
batchInterval(6).//此参数根据batchCount参数，进行测试调整



# 最后

测试环境：

笔记本：固态硬盘，16G内存，4核八线程处理器，mysql5.7

![1579359504028](README.assets/1579359504028.png) 



因为测试环境不同，测试结果肯定不一样，请一定实际进行测试
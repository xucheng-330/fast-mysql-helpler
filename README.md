# What fast-mysql-helper

fast-mysql-helper is a High performance mysql insert data accelerator lib.

# Why Use?

MySQL���ݿ⣬���������ܵ�IO�����ƣ�ͨ���ڰټ���TPS����ǧ����TPS��

Fast_MySQL_Helper�ǳ��죬�ܴﵽ�򼶵�TPS��ͨ�������ķ�ʽ�������������ܣ�����50�������ܣ�

�߲����� ��̬Ӳ�̵ıʼǱ��ﵽ6.5��TPS��
���ӳ٣�200������ӳ�
�������������ã�������



The insertion performance of MySQL database is limited by IO, usually in level 100 TPS or level 1000 TPS.

Fast MySQL helper is very fast and can reach 10000 level TPS. Through batch mode, it can improve insertion performance and 50 times performance!

High concurrency: the notebook of SSD reaches 65000 TPS.

Low latency: 200 ms latency

Lightweight: easy to use, less dependence

# How Use

1��ִ�н������

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

2��׼��ʵ����

```java
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

3�����ô���FastMysqlClient����

```java
//��������Դ����
DruidDataSource datasource = new DruidDataSource();
//ע�����characterEncoding=utf-8&useSSL=false&allowMultiQueries=true
datasource.setUrl("jdbc:mysql://localhost:3306/fast_mysql_demo?characterEncoding=utf-8&useSSL=false&allowMultiQueries=true");
    
datasource.setUsername("root");
datasource.setPassword("123456");
datasource.setDriverClassName("com.mysql.jdbc.Driver");

//ʵ����FastMysqlClient
FastMysqlClient client = new FastMysqlClientBuilder().
dataSource(datasource).//��������Դ
registerEntity(Emp.class).//ע��ʵ����
registerEntity(Dept.class).
showSQL(false).//�����Ƿ��ӡSQL
batchCount(100).//�ص㣺ÿ�����������������
batchInterval(2).//�ص㣺���εļ��ʱ�䣬Ӱ����Ӧ�ӳ�
enableTransferQuotes(false).//�ַ�ת�� '-->\' ���ַ����д��е�����������Ϊtrue��Ӱ������
build();
```

4���ύ��������

```java
Emp m1 = new Emp(null, "�ɸ�" + i, "M", "xucheng@qq.com", 100l);
Dept dept = new Dept("������" ,145,"��õ�һ������������Ϊʲô������ô���뷨�ء�");

client.startMultiFastInsert() //����һ����������
      .add(m1)//���Ҫ���������
      .add(dept)
      .asyncCallback(//ע���첽�ص�����������������Ժ󣬻�ص��� ��ȻҲ������ͬ���ķ�ʽ
           new AsyncCallback() {
              @Override
              public void callback(AsyncResultFuture future,
                                   boolean result, Throwable cause) {
                  if (result){//����ִ�н����true ����ɹ���
                      //����ִ�гɹ����߼�
                  }else {
                      //ִ��ʧ�ܣ����ص��쳣��Ϣ
                      cause.printStackTrace();
                  }
              }
       })
      .commit()//�ύ����ִ�ж�����ȥ
      .get();//�����̣߳�ͬ����ȡ���,�������첽�ķ�ʽ�����ܸ��ߣ�

//���뵥������
final AsyncResultFuture asyncResultFuture = client.fastInsert(m1);
//ͬ����ȡ���
final boolean b = asyncResultFuture.get();

//���뵥�����ݣ��첽��ȡ���, �첽�ķ�ʽ���servlet3.0�첽api��ȡ�������
client.fastInsert(m1, new AsyncCallback() {
    @Override
    public void callback(AsyncResultFuture future, 
                         boolean result, Throwable cause) {

        if (result){//����ִ�н����true ����ɹ���
            //����ִ�гɹ����߼�
        }else {
            //ִ��ʧ�ܣ����ص��쳣��Ϣ
            cause.printStackTrace();
        }
    }
});
```

# ���ܲ��Խ��

ִ��fast-mysql-helper-demo�е�FastMysqlClientTest��������

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

    //ģ��10���߳��ύ����
    private int threadCount = 10;
    //ÿ���߳�ִ��100000������
    private int perThreadTaskCount = 100000;
    //�ܼ�ִ�������� 100��
    private int totalTaskCount = perThreadTaskCount*threadCount;

    @Test
    public void testFastMysqlClient() throws InterruptedException, IOException {

        //��������Դ����ע�����characterEncoding=utf-8&useSSL=false&allowMultiQueries=true
        DruidDataSource datasource = new DruidDataSource();
        datasource.setUrl("jdbc:mysql://localhost:3306/fast_mysql_demo?characterEncoding=utf-8&useSSL=false&allowMultiQueries=true");
        datasource.setUsername("root");
        datasource.setPassword("123456");
        datasource.setDriverClassName("com.mysql.jdbc.Driver");

        //ʵ����FastMysqlClient
        final FastMysqlClient client = new FastMysqlClientBuilder().
                dataSource(datasource).//��������Դ
                registerEntity(Emp.class).//ע��ʵ����
                registerEntity(Dept.class).
                showSQL(false).//�����Ƿ��ӡSQL
                batchCount(500).//�ص㣺ÿ�����������������
                batchInterval(6).//�ص㣺���εļ��ʱ�䣬Ӱ����Ӧ�ӳ�
                enableTransferQuotes(false).//�ַ�ת�� '-->\' ���ַ����д��е�����������Ϊtrue��Ӱ������
                build();

        long globalStart = System.currentTimeMillis();
        for (int i = 0; i < threadCount; i++) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    for (int i = 0; i < perThreadTaskCount; i++) {
                        try {
                            Emp m1 = new Emp(null, "�ɸ�" + i, "M", "xucheng@qq.com", 100l);
                            Dept dept = new Dept("������" ,145,"��õ�һ������������Ϊʲô������ô���뷨�ء�");

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

        //�ȴ���������ִ�����
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
            System.out.println(sdf.format(qpsTaskInfo.start)+ " ====" + qpsTaskInfo.taskCount + "������ʱ" + qpsTaskInfo.time + "����");
        }

        System.out.println("�ܼ���������" + totalTaskCount + "�� ����ʱ�����룩�� " + (globalEnd-globalStart));
        System.out.println("���"+qpsTaskInfos.size()+"������ʱ(����)�� " +(end-start));
        System.out.println("ƽ��ÿ��������:" + (taskCount*1000/(end-start)));


    }
}

```

���ֲ��Խ����

```text
22:24:25:573 ====500������ʱ70����
22:24:25:580 ====500������ʱ70����
22:24:25:587 ====500������ʱ140����
22:24:25:596 ====500������ʱ143����
22:24:25:603 ====500������ʱ131����
22:24:25:611 ====500������ʱ129����
22:24:25:618 ====500������ʱ117����
22:24:25:625 ====500������ʱ118����
22:24:25:632 ====500������ʱ131����
22:24:25:638 ====500������ʱ124����
22:24:25:645 ====500������ʱ125����
22:24:25:652 ====500������ʱ129����
22:24:25:660 ====500������ʱ129����
22:24:25:680 ====500������ʱ137����
22:24:25:690 ====500������ʱ146����
22:24:25:697 ====500������ʱ138����
22:24:25:704 ====500������ʱ134����
22:24:25:711 ====500������ʱ121����
22:24:25:723 ====500������ʱ122����
22:24:25:730 ====482������ʱ112����
�ܼ���������1000000�� ����ʱ�����룩�� 15022
���2001������ʱ(����)�� 14311
ƽ��ÿ��������:69876
```

���ۣ� ÿ��ִ��500���������񣬹�����100�������ݣ���ʱ15�룬ÿ���������������7��ƽ���ӳ�200���룡

ע�⣺��������������������Ӱ�켫��

batchCount(500).//�ص㣺ÿ�����������������
batchInterval(6).//�ص㣺���εļ��ʱ�䣬Ӱ����Ӧ�ӳ�



# ��ͳ���뷽ʽ�����ܲ��Խ��

ִ��fast-mysql-helper-demo�е�SlowMysqlClientTest

```java
public class SlowMysqlClientTest {

    private AtomicLong count = new AtomicLong(0);

    //20���߳�һ���ύ����
    private int threadCount = 20;
    //ÿ���߳��ύ3000������
    private int perThreadTaskCount = 3000;
    //�ܼ��ύ60000������
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
                            Emp m1 = new Emp(null, "�ɸ�" + i, "M", "xucheng@qq.com", 100l);
                            Dept dept = new Dept("������" + i,120,"��һ������������������");

                            final boolean insert = normalInsert.insert(m1, dept);
                            if (insert){
                                count.incrementAndGet();
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    long end = System.currentTimeMillis();
                    System.out.println("ִ���������� " + perThreadTaskCount + ", ��ʱ�����룩��" + (end-start));
                }
            }).start();
        }

        while (!(totalTaskCount==count.intValue())){
            Thread.sleep(1000);
            System.out.println(count.longValue());
        }

        long globalEnd = System.currentTimeMillis();
        System.out.println("�ܼ���������" + totalTaskCount + "�� ����ʱ�����룩�� " + (globalEnd-globalStart));
        System.out.println("ÿ��������: " + totalTaskCount * 1000/(globalEnd-globalStart));
    }
}

```

NormalInsert�����

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

���в�������ִ�н����

```text
ִ���������� 3000, ��ʱ�����룩��43701
ִ���������� 3000, ��ʱ�����룩��43775
ִ���������� 3000, ��ʱ�����룩��43828
ִ���������� 3000, ��ʱ�����룩��44071
ִ���������� 3000, ��ʱ�����룩��44092
ִ���������� 3000, ��ʱ�����룩��44535
ִ���������� 3000, ��ʱ�����룩��44724
ִ���������� 3000, ��ʱ�����룩��45155

�ܼ���������60000�� ����ʱ�����룩�� 46028
ÿ��������: 1303
```



���ۣ�6�������ִ����46�룬 ƽ��ÿ���̵߳���ʱ44�롣ÿ��������1303



# �ܽ�

ʹ��fast-mysql-helper����٣�

ÿ�������� �� 1303  ������  69876 ������������53��������



��ͳ��ʽ�� ƽ��ÿ��������Ҫ14���룬 �����������ӳٱȽ��١�

fast-mysql-helper�ķ�ʽ��ÿ������500��������ҪԼ200������ӳ٣���fast-mysql-helper���������������������Ӧʱ�䣬����ȡ������������ܡ� ���������ȡ� 200�����ڴ��������������ɿ��Խ��ܵģ����ң������ͨ��ָ���������������������е�����

batchCount(500).//ԽС���ӳپ�ԽС,�������ܻ����
batchInterval(6).//�˲�������batchCount���������в��Ե���



# ���

���Ի�����

�ʼǱ�����̬Ӳ�̣�16G�ڴ棬4�˰��̴߳�������mysql5.7

![1579359504028](README.assets/1579359504028.png) 



��Ϊ���Ի�����ͬ�����Խ���϶���һ������һ��ʵ�ʽ��в���
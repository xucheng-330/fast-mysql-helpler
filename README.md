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

//���뵥�����ݣ��첽��ȡ���
client.fastInsert(null, new AsyncCallback() {
    @Override
    public void callback(AsyncResultFuture future, 
                         boolean result, Throwable cause) {

    }
});
```


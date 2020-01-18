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
        datasource.setUrl("jdbc:mysql://localhost:3306/fast_mysql_demo?characterEncoding=utf-8&useSSL=false&allowMultiQueries=true");
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

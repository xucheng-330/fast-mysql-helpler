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

    private int threadCount = 10;
    private int perThreadTaskCount = 1;
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
                batchCount(100).//�ص㣺ÿ�����������������
                batchInterval(2).//�ص㣺���εļ��ʱ�䣬Ӱ����Ӧ�ӳ�
                enableTransferQuotes(false).//�ַ�ת�� '-->\' ���ַ����д��е�����������Ϊtrue��Ӱ������
                build();

        //
        client.fastInsert(null, new AsyncCallback() {
            @Override
            public void callback(AsyncResultFuture future,
                                 boolean result, Throwable cause) {

            }
        });


        long globalStart = System.currentTimeMillis();
        for (int i = 0; i < threadCount; i++) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    for (int i = 0; i < perThreadTaskCount; i++) {
                        try {
                            Emp m1 = new Emp(null, "�ɸ�" + i, "M", "xucheng@qq.com", 100l);
                            Dept dept = new Dept("������fgdgregergergergdfgergegddfgfdgdfgfdgdfgrgregregdfgegergergergergereffqwwddgrgrhry" ,145,"��õ�һ������������Ϊʲô������ô���뷨�ء�");

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

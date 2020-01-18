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
                batchCount(100).//重点：每次批量插入的事务数
                batchInterval(2).//重点：批次的间隔时间，影响响应延迟
                enableTransferQuotes(false).//字符转译 '-->\' ，字符串中带有单引号需配置为true，影响性能
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
                            Emp m1 = new Emp(null, "成哥" + i, "M", "xucheng@qq.com", 100l);
                            Dept dept = new Dept("技术部fgdgregergergergdfgergegddfgfdgdfgfdgdfgrgregregdfgegergergergergereffqwwddgrgrhry" ,145,"多好的一个技术部啊，为什么还有这么多想法呢。");

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
            System.out.println(sdf.format(qpsTaskInfo.start)+ " ====" + qpsTaskInfo.taskCount + "任务，用时" + qpsTaskInfo.time + "毫秒");
        }

        System.out.println("总计任务数：" + totalTaskCount + "， 共用时（毫秒）： " + (globalEnd-globalStart));
        System.out.println("最近"+qpsTaskInfos.size()+"批次用时(毫秒)： " +(end-start));
        System.out.println("平均每秒任务数:" + (taskCount*1000/(end-start)));


    }
}

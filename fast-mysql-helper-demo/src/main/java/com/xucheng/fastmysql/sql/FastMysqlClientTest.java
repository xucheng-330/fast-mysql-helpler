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

        DruidDataSource datasource = new DruidDataSource();
        datasource.setUrl("jdbc:mysql://localhost:3306/ssm_crud?characterEncoding=utf-8&useSSL=false&allowMultiQueries=true");
        datasource.setUsername("root");
        datasource.setPassword("123456");
        datasource.setDriverClassName("com.mysql.jdbc.Driver");

        final FastMysqlClient client = new FastMysqlClientBuilder().
                dataSource(datasource).
                registerEntity(Emp.class).
                registerEntity(Dept.class).
                showSQL(false).
                batchCount(100).
                batchInterval(2).
                enableTransferQuotes(false).//是否转译字符串的单引号，默认不转译
                build();

        long globalStart = System.currentTimeMillis();
        for (int i = 0; i < threadCount; i++) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    for (int i = 0; i < perThreadTaskCount; i++) {
                        try {
                            Emp m1 = new Emp(null, "成哥" + i, "M", "xucheng@qq.com", 100l);
                            Dept dept = new Dept("技术部fgdgregergergergdfgergegddfgfdgdfgfdgdfgrgregregdfgegergergergergereffqwwddgrgrhry" ,null,"好一个技术部！！！！！");

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

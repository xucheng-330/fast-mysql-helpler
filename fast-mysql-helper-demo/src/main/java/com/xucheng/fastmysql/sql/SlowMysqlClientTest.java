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

    //20个线程一起提交事务
    private int threadCount = 20;
    //每个线程提交3000个事务
    private int perThreadTaskCount = 3000;
    //总计提交60000个事务
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

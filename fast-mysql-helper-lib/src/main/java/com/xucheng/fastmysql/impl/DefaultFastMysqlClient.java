package com.xucheng.fastmysql.impl;

import com.xucheng.fastmysql.FastMysqlClient;
import com.xucheng.fastmysql.api.AsyncCallback;
import com.xucheng.fastmysql.api.AsyncResultFuture;
import com.xucheng.fastmysql.api.config.FastMysqlConfig;
import com.xucheng.fastmysql.api.config.stat.QpsTaskInfo;
import com.xucheng.fastmysql.api.exception.FastMysqlException;
import com.xucheng.fastmysql.impl.sql.BatchSQL;

import java.sql.Connection;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class DefaultFastMysqlClient implements FastMysqlClient {

    private final FastMysqlConfig config;


    private ArrayBlockingQueue<DefaultAsyncResultFuture> queue = new ArrayBlockingQueue<>(10000);

    private Executor threadPool = Executors.newFixedThreadPool(32);

    private Thread scheduleThread = new Thread(new Runnable() {
        @Override
        public void run() {
            while (true){
                try {
                    List<DefaultAsyncResultFuture> list = new ArrayList<>(config.getBatchCount());
                    int i = queue.drainTo(list,config.getBatchCount());
                    if (i>0){
                        BatchTask task = new BatchTask(list);
                        threadPool.execute(task);
                    }
                    Thread.sleep(config.getBatchInterval());
                } catch (Throwable e){
                    e.printStackTrace();
                }
            }
        }
    });

    class BatchTask implements Runnable{
        final List<DefaultAsyncResultFuture> list;
        public BatchTask(List<DefaultAsyncResultFuture> list){
            this.list = list;
        }
        @Override
        public void run() {
            if (list!=null&&list.size()>0){
                long start = System.currentTimeMillis();
                BatchSQL batchSQL = new BatchSQL(config);
                for (DefaultAsyncResultFuture drf : list) {
                    if (drf.isSingle()){
                        batchSQL.addRequest(drf.getOriginRequest());
                    }else {
                        List<Object> originRequests = drf.getOriginRequests();
                        for (Object request : originRequests) {
                            batchSQL.addRequest(request);
                        }
                    }
                }

                Throwable cause = null;
                Boolean result = false;

                /*String sql = batchSQL.batchSQL();
                if (config.isShowSQL()){
                    System.out.println(sql);
                }*/
                try(Connection connection = config.getDataSource().getConnection();
                    Statement statement = connection.createStatement();) {

                    connection.setAutoCommit(false);
                    Iterator<String> SQLs = batchSQL.iterator();
                    while (SQLs.hasNext()){
                        String sql = SQLs.next();
                        statement.addBatch(sql);
                        if (config.isShowSQL()){
                            System.out.println(sql);
                        }
                    }
                    statement.executeBatch();
                    connection.commit();

                    result = true;
                } catch (Throwable e){
                    cause = e;
                }

                for (DefaultAsyncResultFuture drf : list) {
                    drf.setResult(result,cause);
                }

                long end = System.currentTimeMillis();
                config.getQpsStat().add(start,list.size(), (int) (end-start));
            }
        }
    }

    public DefaultFastMysqlClient(FastMysqlConfig config){
        this.config = config;
        this.scheduleThread.start();
    }




    @Override
    public AsyncResultFuture fastInsert(Object request, AsyncCallback... callback)  {
        config.assertValidateRequest(request);
        DefaultAsyncResultFuture e = new DefaultAsyncResultFuture(config,request);
        if (callback!=null){
            if (callback.length==1){
                e.asyncCallback(callback[0]);
            }else {
                throw new FastMysqlException("回调函数只可调置一个！！！！");
            }
        }
        try {
            queue.put(e);
        } catch (InterruptedException ex) {
            throw new FastMysqlException(ex);
        }
        return e;
    }

    @Override
    public AsyncResultFuture startMultiFastInsert() {
        DefaultAsyncResultFuture e = new DefaultAsyncResultFuture(config,new FastMysqlClient.BatchRequestComplete(){
            @Override
            public void callback(AsyncResultFuture future) {
                try {
                    queue.put((DefaultAsyncResultFuture) future);
                } catch (InterruptedException ex) {
                    throw new FastMysqlException(ex);
                }
            }
        });
        return e;
    }

    @Override
    public List<QpsTaskInfo> recentlyQPS() {
        return config.getQpsStat().getCurrentStat();
    }


}

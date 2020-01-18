package com.xucheng.fastmysql.impl;

import com.xucheng.fastmysql.FastMysqlClient;
import com.xucheng.fastmysql.api.AsyncCallback;
import com.xucheng.fastmysql.api.AsyncResultFuture;
import com.xucheng.fastmysql.api.config.FastMysqlConfig;
import com.xucheng.fastmysql.api.exception.FastMysqlException;
import com.xucheng.fastmysql.api.unsafe.UnsafeAsyncResultFuture;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

public class DefaultAsyncResultFuture implements UnsafeAsyncResultFuture {

    private static final AtomicLong count = new AtomicLong(0);


    private FutureTask<Boolean> resultFuture = new FutureTask<Boolean>(new Callable<Boolean>() {
        @Override
        public Boolean call() throws Exception {
            return result;
        }
    });

    private volatile AsyncCallback callback = null;

    private AtomicBoolean hasCommit = new AtomicBoolean(false);
    private AtomicBoolean hasCallBack = new AtomicBoolean(false);
    private AtomicBoolean beginCallBack = new AtomicBoolean(false);
    private AtomicReference<AsyncCallback> hasCallbackSet = new AtomicReference<>(null);


    private volatile Boolean result;
    private volatile Throwable cause;
    private Object request;
    private List<Object> requestList;


    private final boolean isSingle;
    private final long requestId = count.incrementAndGet();
    private FastMysqlClient.BatchRequestComplete batch;

    private FastMysqlConfig config;

    public DefaultAsyncResultFuture(FastMysqlConfig config,FastMysqlClient.BatchRequestComplete batch){
        this.isSingle = false;
        this.requestList = new ArrayList<>();
        this.batch = batch;
        this.config = config;
    }

    public DefaultAsyncResultFuture(FastMysqlConfig config,Object request){
        this.request = request;
        this.isSingle = true;
        this.config = config;
    }


    @Override
    public boolean isSingle() {
        return isSingle;
    }

    @Override
    public Object getOriginRequest() {
        return request;
    }

    @Override
    public List<Object> getOriginRequests() {
        return requestList;
    }

    @Override
    public long requestId() {
        return requestId;
    }

    @Override
    public boolean get() {
        try {
            return resultFuture.get();
        } catch (InterruptedException e) {
            throw new FastMysqlException(e);
        } catch (ExecutionException e) {
            throw new FastMysqlException(e);
        }
    }

    @Override
    public boolean get(long timeout)  {
        try {
            return resultFuture.get(timeout, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            throw new FastMysqlException(e);
        } catch (ExecutionException e) {
            throw new FastMysqlException(e);
        } catch (TimeoutException e) {
            throw new FastMysqlException(e);
        }
    }

    @Override
    public Throwable getCause() {
        return cause;
    }

    @Override
    public AsyncResultFuture asyncCallback(AsyncCallback callback) {
        if (hasCallbackSet.compareAndSet(null,callback)){
            this.callback = hasCallbackSet.get();
        }else {
            throw new FastMysqlException("�ص������Ѿ����ù���������");
        }

        if(beginCallBack.get()){//�Ѿ���ʼ�����ˣ���ͬ�����ûص���������
            try {
                Boolean aBoolean = this.resultFuture.get();
                if (hasCallBack.compareAndSet(false,true)){
                    this.callback.callback(this,result,cause);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }

        return this;
    }

    @Override
    public AsyncResultFuture add(Object request) {
        if (hasCommit.get()) throw new FastMysqlException("�����Ѿ��ύ��������");
        this.requestList.add(request);
        return this;
    }

    @Override
    public AsyncResultFuture commit()  {
        if (hasCommit.compareAndSet(false,true)){
            this.batch.callback(this);
        }
        return this;
    }

    public void setResult(Boolean result,Throwable cause){
        if (beginCallBack.compareAndSet(false,true)){
            this.result = result;
            this.cause = cause;
            this.resultFuture.run();
            if(this.callback!=null){
                //��֤�ص�����ֻ����һ��
                if (hasCallBack.compareAndSet(false,true)){
                    callback.callback(this,result,cause);
                }
            }
        }
    }


}

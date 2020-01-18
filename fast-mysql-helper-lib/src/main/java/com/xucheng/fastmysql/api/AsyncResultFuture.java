package com.xucheng.fastmysql.api;

import com.xucheng.fastmysql.api.unsafe.UnsafeAsyncResultFuture;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

public interface AsyncResultFuture {

    public long requestId();
    public boolean get();
    public boolean get(long timeout);
    public Throwable getCause();
    public AsyncResultFuture asyncCallback(AsyncCallback callback);

    public AsyncResultFuture add(Object request);
    public AsyncResultFuture commit();

}

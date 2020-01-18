package com.xucheng.fastmysql.api.unsafe;

import com.xucheng.fastmysql.api.AsyncResultFuture;

import java.util.List;

public interface UnsafeAsyncResultFuture extends AsyncResultFuture {

    public boolean isSingle();
    public Object getOriginRequest();
    public List<Object> getOriginRequests();

}

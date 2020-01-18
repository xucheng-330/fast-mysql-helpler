package com.xucheng.fastmysql.api;

import java.util.List;

public interface AsyncCallback {
    void callback(AsyncResultFuture future,boolean result, Throwable cause);
}

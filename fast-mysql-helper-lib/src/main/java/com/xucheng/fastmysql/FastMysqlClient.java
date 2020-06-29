package com.xucheng.fastmysql;

import com.xucheng.fastmysql.api.AsyncCallback;
import com.xucheng.fastmysql.api.AsyncResultFuture;
import com.xucheng.fastmysql.api.config.stat.QpsStat;
import com.xucheng.fastmysql.api.config.stat.QpsTaskInfo;
import com.xucheng.fastmysql.api.exception.FastMysqlException;

import java.util.List;

public interface FastMysqlClient {

    AsyncResultFuture fastInsert(Object request, AsyncCallback... callback);
    AsyncResultFuture startMultiFastInsert();
    List<QpsTaskInfo> recentlyQPS();

    interface BatchRequestComplete{
        void callback(AsyncResultFuture future);
    }
}

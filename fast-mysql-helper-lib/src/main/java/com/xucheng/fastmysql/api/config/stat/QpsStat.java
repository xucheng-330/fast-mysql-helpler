package com.xucheng.fastmysql.api.config.stat;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

public class QpsStat {

    private AtomicLong id = new AtomicLong(0);
    private QpsTaskInfo[] taskInfos = new QpsTaskInfo[2048];

    public QpsStat() {

    }

    public List<QpsTaskInfo> getCurrentStat(){
        List<QpsTaskInfo> cur = new  ArrayList<QpsTaskInfo>();
        for (int i = 0; i < taskInfos.length; i++) {
            if (taskInfos[i]!=null){
                cur.add(taskInfos[i]);
            }
        }
        Collections.sort(cur);//°´Ê±¼äÉýÐò
        return cur;
    }

    public void add(long start,int rows,int interval){
        long maxId = id.getAndIncrement();
        if (maxId<0){
            maxId = 0;
            id.set(0);
        }
        QpsTaskInfo taskInfo = new QpsTaskInfo(start,rows,interval);
        int i = (int) (maxId & (taskInfos.length-1));
        taskInfos[i] = taskInfo;
    }


}

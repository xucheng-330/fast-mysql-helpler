package com.xucheng.fastmysql.api.config.stat;

public class QpsTaskInfo implements Comparable<QpsTaskInfo>{

    public int taskCount;
    public int time;
    public long start;

    public QpsTaskInfo(long start, int row, int time) {
        this.taskCount = row;
        this.time = time;
        this.start = start;
    }

    @Override
    public int compareTo(QpsTaskInfo o) {
        return (int) (start-o.start);
    }
}

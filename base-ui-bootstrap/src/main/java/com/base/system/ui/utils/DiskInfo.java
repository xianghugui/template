package com.base.system.ui.utils;

import java.io.File;
import java.util.HashMap;

public class DiskInfo {

    static  File[] roots = File.listRoots();

    public static HashMap getDiskPercentUsage(int i){
        HashMap integerDoubleList = null;
        long totalSpace=roots[i].getTotalSpace();
        long freeSpace=roots[i].getFreeSpace();
        long usedSpace=totalSpace-freeSpace;

        double maxvalue= (totalSpace/1024/1024);
        double current= ((usedSpace)/1024/1024);
        double percent=(current/maxvalue)*100;
        double result=Math.round(percent*100.0)/100.0;
        //硬盘使用率
        integerDoubleList.put("percent",result);
        //磁盘容量
        integerDoubleList.put("totalSpace",maxvalue);
        //磁盘已使用
        integerDoubleList.put("usedSpace",current);
        return integerDoubleList;
    }
}

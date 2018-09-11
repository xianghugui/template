package com.base.system.ui.utils;

import java.io.File;

public class DiskInfo {

    static  File[] roots = File.listRoots();

    public static double getDiskPercentUsage(int i){

        long totalSpace=roots[i].getTotalSpace();
        long freeSpace=roots[i].getFreeSpace();
        long usedSpace=totalSpace-freeSpace;

        double maxvalue= (totalSpace/1024/1024);
        double current= ((usedSpace)/1024/1024);
        double percent=(current/maxvalue)*100;
        double result=Math.round(percent*100.0)/100.0;

        return result;
    }
}

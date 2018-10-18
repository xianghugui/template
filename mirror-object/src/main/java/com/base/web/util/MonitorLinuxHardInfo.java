package com.base.web.util;

import com.base.web.bean.UploadValue;
import com.base.web.service.DeleteDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


/**
 * Linux服务器硬盘内存监控
 */

@Configuration
@EnableScheduling
public class MonitorLinuxHardInfo {

    private int THRESHOLD = 90; //清理数据阈值

    private int DELETE_DAY_LENGTH = 4; //删除数据的天数

    @Autowired
    private DeleteDataService deleteDataService;

    /**
     * 定时监控服务器硬盘内存,超过阈值后,清理最早时间段内的数据
     * 每三小时监测一次
     */
    @Scheduled(cron="* * 0/3 * * ? ")
    public void getLinuxMemInfo() {
        if(!FaceFeatureUtil.isWin) {
            try {
                String[] readLineArray = null;
                String getReadLine = null;
                BufferedReader in = null;
                int line = 0;
                int use_rate = 0;

                Runtime rt = Runtime.getRuntime();
                Process p = rt.exec("df -hl");//linux命令 df -hl 查看硬盘空间
                try {
                    in = new BufferedReader(new InputStreamReader(p.getInputStream()));
                    while ((getReadLine = in.readLine()) != null) {
                        line++;
                        if (line != 2) {
                            continue;
                        }
                        int m = 0;
                        readLineArray = getReadLine.split(" ");
                        for (String para : readLineArray) {
                            if (para.trim().length() == 0){
                                continue;
                            }
                            ++m;
                            if (para.endsWith("%")) {
                                if (m == 5) {
                                    use_rate = Integer.parseInt(para.split("%")[0]);
                                    if(use_rate >= THRESHOLD){
                                        UploadValue uploadValue = new UploadValue();
                                        //查询最早一条数据的时间
                                        Date deleteDateStart = deleteDataService.selectFirstOne();
                                        Date deleteDateEnd;

                                        //最早数据日期加上天数后的日期
                                        Calendar cStart = Calendar.getInstance();
                                        cStart.setTime(deleteDateStart);
                                        cStart.add(Calendar.DATE, DELETE_DAY_LENGTH);
                                        deleteDateEnd = cStart.getTime();

                                        //转换时间格式
                                        SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd");
                                        uploadValue.setSearchStart(sd.format(deleteDateStart));
                                        uploadValue.setSearchEnd(sd.format(deleteDateEnd));

                                        //清理规定时间内的数据
                                        deleteDataService.clearData(uploadValue);
                                        System.out.println("定时清理数据结束");
                                    }
                                }
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    in.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


}

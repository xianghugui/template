package com.base.web.util;

import com.base.web.bean.UploadValue;
import com.base.web.service.DeleteDataService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
 *
 * 功能描述: 定时监控服务器硬盘内存,超过阈值后,清理最早时间段内的数据
 * @author FQ
 * @date 10/19/2018 1:55 PM
 *
 */

@Configuration
@EnableScheduling
public class MonitorLinuxHardInfo {

    private int THRESHOLD = 90; //清理数据阈值

    private int DELETE_DAY_LENGTH = 4; //删除数据的天数

    private Logger logger = LoggerFactory.getLogger(MonitorLinuxHardInfo.class);

    @Autowired
    private DeleteDataService deleteDataService;

    @Scheduled(cron = "* * 0/3 * * ? ")
    public void getLinuxMemInfo() {
        //判断系统
        if (!NetDvrInit.isWin) {
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
                            if (para.trim().length() == 0) {
                                continue;
                            }
                            ++m;
                            //获取服务器硬盘信息
                            if (para.endsWith("%")) {
                                if (m == 5) {
                                    use_rate = Integer.parseInt(para.split("%")[0]);
                                    if (use_rate >= THRESHOLD) {
                                        UploadValue uploadValue = new UploadValue();
                                        //查询最早一条数据的日期
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
                                        logger.debug("定时清理，从" + uploadValue.getSearchStart() + "至" + uploadValue.getSearchEnd());
                                        deleteDataService.clearData(uploadValue);
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

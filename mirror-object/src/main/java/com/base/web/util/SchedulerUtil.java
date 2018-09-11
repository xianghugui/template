package com.base.web.util;

import org.quartz.*;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.Map;

@Service
public class SchedulerUtil {

    @Resource
    private Scheduler scheduler;

    public static final String ORDER = "orderGroupName";
    public static final String VIDEOORDER = "videoOrderGroupName";


    /**
     * 添加定时任务
     *
     * @param jobName          不重复的任务名称
     * @param gruopName        Scheduler.ORDER/VIDEOORDER
     * @param params           携带的参数，可在Job内部获取该参数
     * @param jobClass         任务类
     * @param triggerStartTime 多久开始执行
     */
    public void addJob(String jobName, String gruopName, Map<String, Object> params,
                       Class<? extends Job> jobClass, Date triggerStartTime) {
        JobDataMap map = new JobDataMap();
        if (params != null && !params.isEmpty()) {
            map.putAll(params);
        }
        //具体任务
        JobDetail jobDetail = JobBuilder.newJob(jobClass)
                .usingJobData(map)
                .withIdentity(jobName, gruopName)
                .build();

        Trigger trigger = TriggerBuilder.newTrigger()
                .withIdentity(jobName, gruopName)
                .startAt(triggerStartTime)
                .build();

        try {
            // 交由Scheduler安排触发
            scheduler.scheduleJob(jobDetail, trigger);
        } catch (SchedulerException e) {
            e.printStackTrace();
        }

    }

    /**
     * 删除任务
     *
     * @param jobName
     */
    public void removeJob(String jobName, String gruopName) {
        try {
            TriggerKey key = new TriggerKey(jobName, gruopName);
            scheduler.pauseTrigger(key);
            scheduler.unscheduleJob(key);
            scheduler.deleteJob(new JobKey(jobName, gruopName));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 暂停任务
     *
     * @param jobName
     * @param jobGroupName
     * @throws Exception
     */
    public void pauseJob(String jobName, String jobGroupName) {
        try {
            scheduler.pauseJob(JobKey.jobKey(jobName, jobGroupName));
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
    }

    /**
     * 恢复任务
     *
     * @param jobName
     * @param jobGroupName
     * @throws Exception
     */
    public void resumeJob(String jobName, String jobGroupName) {
        try {
            scheduler.resumeJob(JobKey.jobKey(jobName, jobGroupName));
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
    }

    /**
     * 更新定时任务
     * @param jobName
     * @param jobGroupName
     * @param triggerStartTime
     */
    public void rescheduleJob(String jobName, String jobGroupName, Date triggerStartTime) {
        TriggerKey triggerKey = TriggerKey.triggerKey(jobName, jobGroupName);
        // 表达式调度构建器
        Trigger trigger = TriggerBuilder.newTrigger()
                .withIdentity(jobName, jobGroupName)
                .startAt(triggerStartTime)
                .build();

        // 按新的trigger重新设置job执行
        try {
            scheduler.rescheduleJob(triggerKey, trigger);
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
    }


}

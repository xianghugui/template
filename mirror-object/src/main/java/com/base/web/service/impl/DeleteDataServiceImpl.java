package com.base.web.service.impl;

import com.base.web.bean.UploadValue;
import com.base.web.dao.DeleteDataMapper;
import com.base.web.dao.GenericMapper;
import com.base.web.service.DeleteDataService;
import com.base.web.util.DeleteFileUtil;
import com.base.web.util.FaceFeatureUtil;
import com.base.web.util.NetDvrInit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;


@Service("deleteDataService")
public class DeleteDataServiceImpl implements DeleteDataService {

    @Autowired
    private DeleteDataMapper deleteDataMapper;


    /**
     *
     * 功能描述: 多线程删除规定时间内的服务器文件和数据库数据
     * @author FQ
     * @date 10/22/2018 2:08 PM
     * @param uploadValue
     * @return boolean
     *
     */

    @Transactional
    @Override
    public boolean clearData(UploadValue uploadValue) throws ParseException {
        DeleteFileUtil deleteFileUtil = new DeleteFileUtil();
        String currentImagePath;

        //获取服务器人脸图片存储文件夹路径
        if (NetDvrInit.isWin) {
            currentImagePath = System.getProperty("user.dir") + File.separator
                    + "upload" + File.separator + "file" + File.separator;
        } else {
            currentImagePath = "/data/apache-tomcat-8.5.31/bin/upload/file/";
        }

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        List<String> dateList = findDates(sdf.parse(uploadValue.getSearchStart()), sdf.parse(uploadValue.getSearchEnd()));

        //可变大小线程池，按照任务数来分配线程
        ExecutorService executorService = Executors.newCachedThreadPool();
        //删除时间段里的服务器人脸图片存储文件夹(文件夹按时间命名的)
        for (String date : dateList) {
            //按时间段生成线程
            Runnable clearFileThread = () -> {
                deleteFileUtil.delete(currentImagePath + date);
            };
            executorService.execute(clearFileThread);
        }

        uploadValue.setSearchStart(uploadValue.getSearchStart() + " 00:00:00");
        uploadValue.setSearchEnd(uploadValue.getSearchEnd() + " 23:59:59");

        //删除相关数据库表数据
        Runnable deleteAssociationBlackList = () -> {
            deleteDataMapper.deleteAssociationBlackList(uploadValue);
        };

        Runnable deleteFaceFeature = () -> {
            deleteDataMapper.deleteFaceFeature(uploadValue);
        };

        Runnable deleteFaceImage = () -> {
            deleteDataMapper.deleteFaceImage(uploadValue);
        };

        Runnable deleteResource = () -> {
            deleteDataMapper.deleteResource(uploadValue);
        };

        Runnable deleteUploadFeature = () -> {
            deleteDataMapper.deleteUploadFeature(uploadValue);
        };

        executorService.execute(deleteAssociationBlackList);
        executorService.execute(deleteFaceFeature);
        executorService.execute(deleteFaceImage);
        executorService.execute(deleteResource);
        executorService.execute(deleteUploadFeature);

        //不允许再向线程池中增加线程
        executorService.shutdown();

        //判断是否所有线程已经执行完毕
        try {
            while (!executorService.awaitTermination(10, TimeUnit.SECONDS)) {
                System.out.println("线程池没有关闭");
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return executorService.isTerminated();
    }

    //JAVA获取某段时间内的所有日期
    public List<String> findDates(Date dStart, Date dEnd) {
        SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd");

        Calendar cStart = Calendar.getInstance();
        cStart.setTime(dStart);
        List dateList = new ArrayList();
        //起始日期
        dateList.add(sd.format(dStart));
        // 此日期是否在指定日期之后
        while (dEnd.after(cStart.getTime())) {
            // 根据日历的规则，为给定的日历字段添加或减去指定的时间量
            cStart.add(Calendar.DAY_OF_MONTH, 1);
            dateList.add(sd.format(cStart.getTime()));
        }
        return dateList;
    }



    @Override
    public Date selectFirstOne() {
        return deleteDataMapper.selectFirstOne();
    }


}

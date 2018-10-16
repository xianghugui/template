package com.base.web.service.impl;

import com.base.web.bean.UploadValue;
import com.base.web.dao.DeleteDataMapper;
import com.base.web.dao.GenericMapper;
import com.base.web.service.DeleteDataService;
import com.base.web.util.DeleteFileUtil;
import com.base.web.util.FaceFeatureUtil;
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

@Service("deleteDataService")
public class DeleteDataServiceImpl implements DeleteDataService {

    @Autowired
    private DeleteDataMapper deleteDataMapper;

    @Transactional
    @Override
    public void clearData(UploadValue uploadValue) throws ParseException {
        String currentImagePath;

        //获取服务器人脸图片存储文件夹路径
        if (FaceFeatureUtil.isWin) {
            currentImagePath = System.getProperty("user.dir") + File.separator
                    + "upload" + File.separator + "file" + File.separator;
        } else {
            currentImagePath = "/data/apache-tomcat-8.5.31/bin/upload/file/";
        }

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        List<String> dateList = findDates(sdf.parse(uploadValue.getSearchStart()),sdf.parse(uploadValue.getSearchEnd()));

        //删除时间段里的服务器人脸图片存储文件夹(文件夹按时间命名的)
        for(String date:dateList){
            //按时间段生成线程
            ClearFileThread clearFileThread = new ClearFileThread(currentImagePath+date);
            Thread thread = new Thread(clearFileThread);
            thread.start();
        }

        uploadValue.setSearchStart(uploadValue.getSearchStart()+" 00:00:00");
        uploadValue.setSearchEnd(uploadValue.getSearchEnd()+" 23:59:59");
        //删除数据库表数据

        deleteDataMapper.deleteAssociationBlackList(uploadValue);
        deleteDataMapper.deleteFaceImage(uploadValue);
        deleteDataMapper.deleteFaceFeature(uploadValue);
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

    //多线程删除文件
    public class ClearFileThread implements Runnable{

        private String filePath;
        private DeleteFileUtil deleteFileUtil = new DeleteFileUtil();

        public ClearFileThread(String filePath){
            this.filePath = filePath;
        }

        @Override
        public void run(){
            deleteFileUtil.delete(filePath);
        }
    }


}

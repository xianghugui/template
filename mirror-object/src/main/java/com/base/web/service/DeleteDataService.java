package com.base.web.service;

import com.base.web.bean.AimsMessageDTO;
import com.base.web.bean.UploadValue;

import java.text.ParseException;
import java.util.Date;
import java.util.List;

public interface DeleteDataService {
    boolean clearData(UploadValue uploadValue) throws ParseException;

    Date selectFirstOne();
}

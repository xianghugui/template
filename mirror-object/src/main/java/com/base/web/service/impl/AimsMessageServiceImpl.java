package com.base.web.service.impl;

import com.base.web.bean.AimsMessageDTO;
import com.base.web.bean.UploadValue;
import com.base.web.dao.AimsMessageMapper;
import com.base.web.service.AimsMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("aimsMessageService")
public class AimsMessageServiceImpl implements AimsMessageService {

    @Autowired
    private AimsMessageMapper aimsMessageMapper;

    @Override
    public List<AimsMessageDTO> listAimsMessage(UploadValue uploadValue) {
        return aimsMessageMapper.listAimsMessage(uploadValue);
    }
}

package com.base.web.service;

import com.base.web.bean.AimsMessageDTO;
import com.base.web.bean.UploadValue;

import java.util.List;

public interface AimsMessageService {
    List<AimsMessageDTO> listAimsMessage(UploadValue uploadValue);

    int listAimsMessageTotal(UploadValue uploadValue);
}

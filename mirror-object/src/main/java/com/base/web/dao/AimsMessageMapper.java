package com.base.web.dao;

import com.base.web.bean.AimsMessageDTO;
import com.base.web.bean.UploadValue;

import java.util.List;

public interface AimsMessageMapper {
    List<AimsMessageDTO> listAimsMessage(UploadValue uploadValue);

    int listAimsMessageTotal(UploadValue uploadValue);
}

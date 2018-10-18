package com.base.web.controller;


import com.base.web.bean.UploadValue;
import com.base.web.core.authorize.annotation.Authorize;
import com.base.web.core.logger.annotation.AccessLogger;
import com.base.web.core.message.ResponseMessage;
import com.base.web.service.DeleteDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.util.Date;

@RestController
@RequestMapping(value = "/clearData")
@AccessLogger("数据清理")
@Authorize(module = "clearData")
public class DeleteDataController{

    @Autowired
    private DeleteDataService deleteDataService;

    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    @AccessLogger("清空某时间段数据库数据")
    @Authorize(action = "D")
    public ResponseMessage clearData(@RequestBody UploadValue uploadValue) throws ParseException {
        return ResponseMessage.ok(deleteDataService.clearData(uploadValue));
    }


}

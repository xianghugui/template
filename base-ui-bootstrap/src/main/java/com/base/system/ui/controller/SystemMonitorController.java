package com.base.system.ui.controller;


import com.google.common.collect.ImmutableMap;
import com.base.system.ui.utils.CPUInfo;
import com.base.system.ui.utils.DiskInfo;
import com.base.system.ui.utils.RAMInfo;
import com.base.web.core.message.ResponseMessage;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping(value = "/monitor")
public class SystemMonitorController {

    @RequestMapping(value = "/info")
    public ResponseMessage info() {

        double cpuLoad = CPUInfo.getProcessCpuLoad();

        double diskLoad = DiskInfo.getDiskPercentUsage(0);

        double ramLoad = RAMInfo.getRamPercentUsage();

        Map<String, Object> data = ImmutableMap.of("cpu", cpuLoad, "disk", diskLoad, "ram", ramLoad);

        return ResponseMessage.ok(data);
    }
}

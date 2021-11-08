package com.qycloud.iot.server;

import com.qpaas.iot.qlink_sdk.dto.RealDataDO;
import com.qpaas.iot.qlink_sdk.dto.SensorDTO;
import com.qpaas.iot.qlink_sdk.service.PrepareDataService;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @Author: WangHao
 * @Date: 2021/11/04/11:28
 * @Description:
 */
@Component
public class IotServer implements PrepareDataService {
    @Override
    public List<RealDataDO> prepareRealData() {

        List<RealDataDO> sensorRealDataList = AnyLinkServer.getSensorRealDataList();
        return sensorRealDataList;
    }

    @Override
    public List<SensorDTO> prepareSensor() {

        List<SensorDTO> sensorInfoList = AnyLinkServer.getSensorInfoList();
        return sensorInfoList;
    }
}

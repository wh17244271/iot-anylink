package com.qycloud.iot.domain;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @Author: WangHao
 * @Date: 2021/11/04/19:44
 * @Description:
 */
@Data
@Accessors(chain = true)
public class DeviceData {


    private String serialNumber;
    private String deviceId;
    private String deviceName;
    private String condition;

}

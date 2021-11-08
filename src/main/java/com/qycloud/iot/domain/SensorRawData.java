package com.qycloud.iot.domain;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @Author: WangHao
 * @Date: 2021/11/04/16:50
 * @Description:
 */
@Data
@Accessors(chain = true)
public class SensorRawData {

    private String deviceName;
    private String devid;
    private String htime;
    private String itemid;
    private String itemname;
    private String quality;
    private int readOnly;
    private String val;
    private long timestamp;
    private String datatype_id;
}

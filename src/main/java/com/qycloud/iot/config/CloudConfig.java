package com.qycloud.iot.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * @Author: WangHao
 * @Date: 2021/11/08/9:32
 * @Description:
 */
@Configuration
@Data
public class CloudConfig {

    @Value("${yunyun.url.token_url}")
    private  String tokenUrl;

    @Value("${yunyun.url.agentList_url}")
    private  String agentListUrl;

    @Value("${yunyun.url.currentdata_url}")
    private  String currentdataUrl;

    @Value("${yunyun.config.tenantEname}")
    private  String tenantEname;

    @Value("${yunyun.config.username}")
    private  String username;

    @Value("${yunyun.config.password}")
    private  String password;

    @Value("${yunyun.config.serialNumber}")
    private  String serialNumber;

    @Value("${yunyun.config.agentId}")
    private  String agentId;

    @Value("${yunyun.config.deviceIds}")
    private  String[] deviceIds;

    @Value("${yunyun.config.deviceIdsFilter}")
    private  boolean deviceIdsFilter = false;

    @Value("${yunyun.config.sensorFilter}")
    private  boolean sensorFilter = false;

    @Value("${yunyun.config.filePath}")
    private  String filePath ;



}

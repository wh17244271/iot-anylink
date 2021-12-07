package com.qycloud.iot.server;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.qpaas.iot.qlink_sdk.dto.RealDataDO;
import com.qpaas.iot.qlink_sdk.dto.SensorDTO;
import com.qycloud.iot.config.CloudConfig;
import com.qycloud.iot.domain.DeviceData;
import com.qycloud.iot.domain.SensorRawData;
import com.qycloud.iot.utils.RestTemplateUtils;
import com.qycloud.iot.utils.StaticInterfaceUtils;
import com.qycloud.iot.utils.TxtUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @Author: WangHao
 * @Date: 2021/11/04/11:02
 * @Description:
 */
//@Component
@Slf4j
public class AnyLinkServer {



//    @Autowired
//    RestTemplate restTemplate;

//    private static String url = "http://cloud.anylink.io:8600";
//    //    private static String url = "https://cloud.anylink.io:8443";
//
//    private static String getTokenUrl = url + "/user/getToken";
//
//    //分页获取网关-设备列表
////    private static String agentList = url + "/agentList/pagination?token={token}&page={page}&perPage={perPage}&agentId={agentId}&condition={condition}";
//    private static String agentList = url + "/agentList/pagination";
//
//    //分页获取单个设备的实时数据
//    private static String currentdata = url + "/currentdata/pagination";
//
//
//
//    private static String tenantEname = "jingshen";
////    private static String username = "admin";
//    private static String username = "anyuan";
////    private static String password = "jsyh@3299";
//    private static String password = "13851778645";
//
//    private static String serialNumber = "c";




//    private static String[] deviceIds = {"1444563972","1444563969"};




    private static CloudConfig cloudConfig = StaticInterfaceUtils.getInterface(CloudConfig.class);




    public static Cache<String, String> tokenCache = CacheBuilder.newBuilder()
            .expireAfterWrite(5, TimeUnit.HOURS)
            .build();


    public static Cache<String, List<DeviceData>> devicesCache = CacheBuilder.newBuilder()
            .expireAfterWrite(5, TimeUnit.HOURS)
            .build();


//     static CloudConfig cloudConfig =  StaticInterfaceUtils.getInterface(CloudConfig.class);



    public static void main(String[] args) {
        try {
            List<RealDataDO> sensorRealDataList = getSensorRealDataListByDevices();
//            getSensorList();
//            List<DeviceData> deviceList = getDeviceList();
            System.out.println(sensorRealDataList);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }




    public static  List<RealDataDO> getSensorRealDataListByDevices(){

        List<RealDataDO> results  = new ArrayList<>();
        RealDataDO result = null;

        List<DeviceData> deviceList = getDeviceList();

        if (null == deviceList || deviceList.isEmpty()) return results;

        for (DeviceData data : deviceList){
            String deviceId = data.getDeviceId();
            List<SensorRawData> sensorList = getSensorList(deviceId);

            if (null == sensorList || sensorList.isEmpty()) continue;

            for (SensorRawData rawData : sensorList){

                String datatype_id = rawData.getDatatype_id();

                String type = "double";
                if ("s".equals(datatype_id)){
                    type = "string";
                }

                result = new RealDataDO();
                result.setCode(cloudConfig.getTenantEname()+"_"+data.getSerialNumber()+"_"+rawData.getDevid()+"_"+rawData.getItemid());
                result.setType(type);
                result.setRawData(rawData.getVal());
                result.setValue(rawData.getVal());
                result.setStatus(1);
                result.setTime(rawData.getTimestamp());
                results.add(result);
            }

        }



        return results;

    }


    public static  List<SensorDTO> getSensorInfoListByDevices(){
        // 1444563972

        List<SensorDTO> results  = new ArrayList<>();
        SensorDTO result = null;



        List<DeviceData> deviceList = getDeviceList();

        if (null == deviceList || deviceList.isEmpty()) return results;


        for (DeviceData data :deviceList){
            String deviceId = data.getDeviceId();
            List<SensorRawData> sensorList = getSensorList(deviceId);

            if (null == sensorList || sensorList.isEmpty()) continue;

            for (SensorRawData rawData : sensorList){
                result = new SensorDTO();
                result.setCode(cloudConfig.getTenantEname()+"_"+data.getSerialNumber()+"_"+rawData.getDevid()+"_"+rawData.getItemid());
                result.setName(rawData.getItemname());
                result.setType("cloud");
                result.setUnit("");
                results.add(result);
            }

        }


        return results;

    }



    /**
     * 获取设备列表
     * @param
     * @Author: WangHao
     */
    public static  List<DeviceData> getDeviceList(){
        String jsonStr = curlDeviceUrl();
        JSONObject json =(JSONObject) JSONObject.parse(jsonStr);
        JSONObject result =(JSONObject)  json.get("result");

        JSONArray datas = (JSONArray)result.get("data");
        Iterator<Object> iterator = datas.stream().iterator();
        List<DeviceData> all = new ArrayList<>();
        while (iterator.hasNext()){
            JSONObject wangguan = (JSONObject)iterator.next();
            String data = JSONArray.toJSONString(wangguan.get("deviceList"));

            List<DeviceData> sensorRawData1 = JSONArray.parseArray( data, DeviceData.class);
            all.addAll(sensorRawData1);

        }

        boolean filter = cloudConfig.isDeviceIdsFilter();
        if (filter){
            List<String> strings = Arrays.asList(cloudConfig.getDeviceIds());
            all.removeIf(a->!strings.contains(a.getDeviceId()));
        }


        return all;
//
    }


    /**
     * 通过设备 id 获取点位列表
     * @param deviceId
     * @Author: WangHao
     */
    public  static  List<SensorRawData> getSensorList(String deviceId){

        try {
            HttpHeaders headers = new HttpHeaders();

            headers.setContentType(MediaType.APPLICATION_JSON);
            String token = getToken(cloudConfig.getUsername());
            Map<String, Object> params = new HashMap<>();
            params.put("token", token);
            params.put("page", "1");
            params.put("perPage", "2000");
            params.put("deviceId", deviceId);
            params.put("itemName", "");
//            params.put("condition", "1");

            String forObject = RestTemplateUtils.getForObject(cloudConfig.getCurrentdataUrl(), params);
            String jsonStr = RestTemplateUtils.requestGettUri(forObject, headers, params);
            JSONObject json =(JSONObject) JSONObject.parse(jsonStr);
            JSONObject result =(JSONObject)  json.get("result");
            String data = JSONArray.toJSONString(result.get("data"));


            List<SensorRawData> sensorRawData1 = JSONArray.parseArray( data, SensorRawData.class);


            if(cloudConfig.isSensorFilter()){
                if (null == sensorRawData1 || sensorRawData1.isEmpty()) return sensorRawData1;

                String content = TxtUtils.readTxt(cloudConfig.getFilePath());

                if (null == content || content.isEmpty()) return sensorRawData1;

                String[] split = content.split("~");
                List<String> sensorNameList = Arrays.asList(split);

                List<SensorRawData> filterList = new ArrayList<>();
                for (SensorRawData sensorRawData : sensorRawData1) {
                    String itemname = sensorRawData.getItemname();

                    if (sensorNameList.contains(itemname)){
                        filterList.add(sensorRawData);
                    }
                }
                return filterList;
            }


            return sensorRawData1;

        }catch (Exception e){
            e.printStackTrace();
            return null;
        }



    }



    public static  String curlDeviceUrl() {
        String msg = "";

        try {

            HttpHeaders headers = new HttpHeaders();

            headers.setContentType(MediaType.APPLICATION_JSON);
            String token = getToken(cloudConfig.getUsername());
            Map<String, Object> params = new HashMap<>();
            params.put("token", token);
            params.put("page", "1");
            params.put("perPage", "1000");
            params.put("agentId", cloudConfig.getAgentId());
//            params.put("condition", "1");

            String forObject = RestTemplateUtils.getForObject(cloudConfig.getAgentListUrl(), params);
            msg = RestTemplateUtils.requestGettUri(forObject, headers, params);

//            RestTemplate restTemplate = new RestTemplate();
//
//
//            ResponseEntity<JSONObject> body = restTemplate.exchange(forObject, HttpMethod.GET, null, JSONObject.class, params);
//
//            JSONObject body1 = body.getBody();
//            System.out.println(body1.toJSONString());

        } catch (Exception e) {
            e.printStackTrace();
        }

        return msg;

    }


    public  static String getToken(String username) throws Exception {
        String token = tokenCache.getIfPresent(username);

        if (null == token || token.isEmpty()){
            token = getTokenUrl();
            tokenCache.put(username,token);
        }

        return token;


    }

    private static  String getTokenUrl() throws Exception {

        String s = tokenCache.getIfPresent(cloudConfig.getUsername());

        if (null == s || s.isEmpty()){

        }


        String token = null;

        HttpHeaders headers = new HttpHeaders();

        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> params = new HashMap<>();
        params.put("tenantEname", cloudConfig.getTenantEname());
        params.put("name", cloudConfig.getUsername());
        params.put("password", cloudConfig.getPassword());

        params.put("hash", "huaianshiyuancailuyo");

        try {
            ResponseEntity<String> stringResponseEntity = RestTemplateUtils.requestPostAll(cloudConfig.getTokenUrl(), headers, params);
            String body = stringResponseEntity.getBody();

            JSONObject parse = (JSONObject) JSONObject.parse(body);

            String code = String.valueOf(parse.get("status"));
            if ("100".equals(code)) {
                return String.valueOf(parse.get("data"));
            } else {
                throw new Exception(body);
            }

        } catch (Exception e) {
            log.error("getToken error:", e);
            throw new Exception(e);
        }

    }


}

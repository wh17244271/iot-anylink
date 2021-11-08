package com.qycloud.iot.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.ssl.TrustStrategy;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import javax.net.ssl.SSLContext;
import java.nio.charset.StandardCharsets;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.Iterator;
import java.util.Map;

/**
 * @Author: WangHao
 * @Date: 2021/08/20/10:15
 * @Description:
 */
public class RestTemplateUtils {
    private final static int connectTime= 1000*5;
    private final static int readTime= 1000*20;

    /**
     * 请求
     *
     * @param url
     * @param headers
     * @param body
     * @return
     */

    public static JSONObject requestPostUri(String url, HttpHeaders headers, String body) throws Exception {

        HttpEntity request = new HttpEntity(body, headers);
        ResponseEntity<String> stringResponseEntity = null;


        try {


            RestTemplate restTemplate = getInstance();


//            restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory());
            stringResponseEntity = restTemplate.postForEntity(url, request, String.class);

            HttpHeaders headers1 = stringResponseEntity.getHeaders();


            String body2 = stringResponseEntity.getBody();
            JSONObject body1 = JSON.parseObject(body2);

            return body1;
        } catch (HttpClientErrorException e) {
            String message = e.getResponseBodyAsString();

            JSONObject jsonObject = JSON.parseObject(message);
            return jsonObject;
        } catch (HttpServerErrorException a) {
            throw new Exception(a);
        } catch (Exception a) {
            throw new Exception(a);
        }


    }


    public static ResponseEntity<String> requestPostAll(String url, HttpHeaders headers, Object body) throws Exception {

        HttpEntity request = new HttpEntity(body, headers);
        ResponseEntity<String> stringResponseEntity = null;


        try {

            RestTemplate restTemplate = getInstance();
            //添加字符集


            stringResponseEntity = restTemplate.postForEntity(url, request, String.class);

            return stringResponseEntity;
        } catch (HttpClientErrorException e) {
            throw new Exception(e);
        } catch (HttpServerErrorException e) {
            throw new Exception(e);
        } catch (Exception e) {
            throw new Exception(e);
        }


    }


    public static String requestGettUri(String url, HttpHeaders headers, Map<String, Object> params) throws Exception {

        try {
            HttpEntity request = new HttpEntity(null, headers);
            RestTemplate restTemplate = getInstance();
            ResponseEntity<String> body = restTemplate.exchange(url, HttpMethod.GET, request, String.class, params);

//        JSONObject body = restTemplate.getForObject(url, JSONObject.class, request, params);

            return body.getBody();
        } catch (Exception e) {
            throw new Exception(e);
        }

    }

    /**
     * 封装的get请求，暂时只支持map传参，并且value只支持基本类型和String
     *
     * @param url
     * @param object
     * @return
     */
    public static String getForObject(String url, Object object) {
        StringBuffer stringBuffer = new StringBuffer(url);
        if (object instanceof Map) {
            Iterator iterator = ((Map) object).entrySet().iterator();
            if (iterator.hasNext()) {
                stringBuffer.append("?");
                Object element;
                while (iterator.hasNext()) {
                    element = iterator.next();
                    Map.Entry<String, Object> entry = (Map.Entry) element;
                    //过滤value为null，value为null时进行拼接字符串会变成 "null"字符串
                    if (entry.getValue() != null) {
                        stringBuffer.append(element).append("&");
                    }
                    url = stringBuffer.substring(0, stringBuffer.length() - 1);
                }
            }
        } else {
            throw new RuntimeException("url请求:" + url + "请求参数有误不是map类型");
        }
        return url;
    }

    public static void requestPutUri(String url, HttpHeaders headers, String params) throws Exception {
        try {
            HttpEntity request = new HttpEntity(params, headers);
            RestTemplate restTemplate = getInstance();
            ResponseEntity<String> exchange = restTemplate.exchange(url, HttpMethod.PUT, request, String.class);
        } catch (Exception e) {
            throw new Exception(e);
        }


    }


    /**
     * 获取实例
     * @param
     * @Author: WangHao
     */
    private   static RestTemplate getInstance(){
        RestTemplate restTemplate = new RestTemplate();
        //添加字符集
        restTemplate.getMessageConverters().set(1, new StringHttpMessageConverter(StandardCharsets.UTF_8));

        return restTemplate;
    }


    /***
     * 获取去除 SSL验证 实例
     * @param
     * @Author: WangHao
     */
    private  static RestTemplate getInstanceexcludeSSL() throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException {
        HttpComponentsClientHttpRequestFactory httpComponentsClientHttpRequestFactory = generateHttpRequestFactory();

        RestTemplate restTemplate = new RestTemplate(httpComponentsClientHttpRequestFactory);
        //添加字符集
        restTemplate.getMessageConverters().set(1, new StringHttpMessageConverter(StandardCharsets.UTF_8));
        return restTemplate;
    }



    /***
     * 解决ssl 验证
     * @param
     * @Author: WangHao
     */
    private static HttpComponentsClientHttpRequestFactory generateHttpRequestFactory()
            throws NoSuchAlgorithmException, KeyManagementException, KeyStoreException {
        TrustStrategy acceptingTrustStrategy = (x509Certificates, authType) -> true;
        SSLContext sslContext = SSLContexts.custom().loadTrustMaterial(null, acceptingTrustStrategy).build();
        SSLConnectionSocketFactory connectionSocketFactory = new SSLConnectionSocketFactory(sslContext,
                new NoopHostnameVerifier());
        HttpClientBuilder httpClientBuilder = HttpClients.custom();
        httpClientBuilder.setSSLSocketFactory(connectionSocketFactory);
        CloseableHttpClient httpClient = httpClientBuilder.build();
        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();
        factory.setHttpClient(httpClient);

        factory.setConnectTimeout(connectTime);
        factory.setReadTimeout(readTime);

        return factory;
    }

}

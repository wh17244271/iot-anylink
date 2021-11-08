package com.qycloud.iot.utils;


import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class StaticInterfaceUtils implements ApplicationContextAware {
    private static ApplicationContext applicationContext = null;


    private String test_master;

    /**
     * 该方法为需要哪个接口，手动在这儿配置，不够高效
     */
    @PostConstruct
    public void init() {
//        StaticInterfaceUtils.testServiceStatic = testService;
    }


    /**
     * 通过传递接口，返回接口实例
     *
     * @param cl
     * @param <T>
     * @return
     */
    public static <T> T getInterface(Class<T> cl) {
        T bean =  applicationContext.getBean(cl);// 注意是UserServiceI ， 不是UserServiceImpl
        return bean;
    }


    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        if (StaticInterfaceUtils.applicationContext == null) {
            StaticInterfaceUtils.applicationContext = applicationContext;
        }
    }

}

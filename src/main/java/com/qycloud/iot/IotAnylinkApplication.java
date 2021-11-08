package com.qycloud.iot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"com.qycloud.iot.*","com.qpaas.*"})
public class IotAnylinkApplication {

    public static void main(String[] args) {
        SpringApplication.run(IotAnylinkApplication.class, args);
    }

}

package org.falcon.instanceservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients(basePackages = "org.falcon.instanceservice.client")
public class InstanceServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(InstanceServiceApplication.class, args);
    }

}

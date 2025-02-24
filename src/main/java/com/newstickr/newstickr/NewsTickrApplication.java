package com.newstickr.newstickr;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class NewsTickrApplication {

    public static void main(String[] args) {
        SpringApplication.run(NewsTickrApplication.class, args);
    }

}

package com.example.demo;

import org.springframework.boot.Banner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

/**
 * 启动类
 */

@SpringBootApplication
public class DemoApplication extends SpringBootServletInitializer {

    /**
     * jar包启动类
     * @param args
     */
    public static void main(String[] args) {
        SpringApplication application = new SpringApplication(DemoApplication.class);
        application.setBannerMode(Banner.Mode.OFF);
        SpringApplication.run(DemoApplication.class, args);
    }

    /**
     * war包启动类
     * @param application
     * @return
     */
    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        application.bannerMode(Banner.Mode.OFF);
        SpringApplicationBuilder applicationBuilder = application.sources(DemoApplication.class);
        return applicationBuilder;
    }
}


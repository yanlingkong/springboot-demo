package com.example.demo.common.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * Swagger2配置
 * Created by liuc on 2018/9/01.
 */
@Configuration
@EnableSwagger2
@Profile({"dev","test"})//在生产环境不开启
public class Swagger2Configuration {

    @Bean
    public Docket createRestApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .groupName("Demo API")
                .apiInfo(apiInfo())
                .select()//访问demo下controller下的方法
                .apis(RequestHandlerSelectors.basePackage("com.example.demo.modules"))
                .paths(PathSelectors.any())
                .build();
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("Demo APIs")
                .description("Demo APIs")
                .termsOfServiceUrl("http://www.baidu.com/")
                .contact(new Contact("Demo","http://www.baidu.com/",""))
                .version("2.0")
                .build();
    }
}

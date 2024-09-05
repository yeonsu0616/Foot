package com.example.foot.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
    @Value("${uploadPath}") // application.properties 설정한 uploadPath
    String uploadPath;
    //uploadPath = "C:/shop
    // images/item/xxx.jpg
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry){
        // /item/images/item/ 경로로 접근 시 /home/ec2-user/Test/item/에서 파일을 찾음
        registry.addResourceHandler("/item/images/item/**")
                .addResourceLocations("file:" + uploadPath + "/item/");
        // /images/item/ 경로로 접근 시 /home/ec2-user/Test/item/에서 파일을 찾음
        registry.addResourceHandler("/images/item/**")
                .addResourceLocations("file:" + uploadPath + "/item/");
    }

}

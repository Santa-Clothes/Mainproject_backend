package com.kdt03.fashion_api.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(org.springframework.web.servlet.config.annotation.CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("*")
                .allowedMethods("*")
                .allowedHeaders("*");
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {

        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:///C:/uploads/");
        registry.addResourceHandler("/images/**")
                .addResourceLocations("file:///C:/clothimage/");
        registry.addResourceHandler("/**")
                .addResourceLocations("classpath:/static/");
    }

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {

        registry.addViewController("/product-list").setViewName("productList");
        registry.addViewController("/test-page").setViewName("uploadTest");
        registry.addViewController("/profile_test").setViewName("profile_test");
        registry.addViewController("/").setViewName("forward:/index.html");
    }

}
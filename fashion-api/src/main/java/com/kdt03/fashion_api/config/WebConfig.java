package com.kdt03.fashion_api.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {

        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:///C:/workspace_fashion/uploads/");
     registry.addResourceHandler("/images/**")
                .addResourceLocations("file:///C:/clothimage/");
    } 

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {

        registry.addViewController("/product-list").setViewName("productList");
    }
} 
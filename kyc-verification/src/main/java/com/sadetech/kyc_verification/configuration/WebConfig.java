package com.sadetech.kyc_verification.configuration;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Ensure this matches your file path
        registry.addResourceHandler("/static/uploads/**")
                .addResourceLocations("classpath:/static/uploads/");

    }
}
package com.tomato.market.config;

import com.tomato.market.notification.NotificationInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@RequiredArgsConstructor
@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final NotificationInterceptor notificationInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(notificationInterceptor).excludePathPatterns("/css/**", "/js/**", "/img/**", "/webjars/**", "/**/favicon.ico", "/fonts/**", "/icon/**", "/node_modules/**", "/summernote_image/**", "/node_modules/**");
    }
}

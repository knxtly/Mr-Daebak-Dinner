package com.devak.mrdaebakdinner.config;

import com.devak.mrdaebakdinner.interceptor.CustomerLoginCheckInterceptor;
import com.devak.mrdaebakdinner.interceptor.OrderLoginCheckInterceptor;
import com.devak.mrdaebakdinner.interceptor.StaffLoginCheckInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    private CustomerLoginCheckInterceptor customerLoginCheckInterceptor;

    @Autowired
    private StaffLoginCheckInterceptor staffLoginCheckInterceptor;

    @Autowired
    private OrderLoginCheckInterceptor orderLoginCheckInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(customerLoginCheckInterceptor)
                .addPathPatterns("/customer/orders/**", "/customer/main"); // 로그인 체크 필요 경로

        registry.addInterceptor(staffLoginCheckInterceptor)
                .addPathPatterns("/staff/**")
                .excludePathPatterns("/staff", "/staff/login", "/staff/logout");

        registry.addInterceptor(orderLoginCheckInterceptor)
                .addPathPatterns("/orders/**");
    }
}

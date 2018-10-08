package com.base.web.bootstrap;

import org.springframework.context.annotation.Configuration;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Project: base-framework
 * Author: Sendya <18x@loacg.com>
 * Date: 2017/8/16 9:44
 */
@Configuration
public class WebInitializer implements WebMvcConfigurer {

    /**
     * 忽略 路由路径大小写区分的问题
     * Author Sendya <18x@loacg.com>
     * @param configurer
     */
    @Override
    public void configurePathMatch(PathMatchConfigurer configurer) {
        AntPathMatcher matcher = new AntPathMatcher();
        matcher.setCaseSensitive(false);
        configurer.setPathMatcher(matcher);
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/webcomponents/**").addResourceLocations("classpath:/webcomponents/");
    }
}

package com.base.web.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.base.web.core.message.FastJsonHttpMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;

/**
 * Created by    on 2016-01-22 0022.
 */
@Configuration
public class MessageConverterConfiguration {

    @Bean
    public HttpMessageConverter<Object> converter() {
        FastJsonHttpMessageConverter converter = new FastJsonHttpMessageConverter();
        converter.setFeatures(
                /*SerializerFeature.WriteNullListAsEmpty,
                SerializerFeature.WriteNonStringKeyAsString,
                SerializerFeature.WriteNonStringValueAsString,*/
                SerializerFeature.WriteNullStringAsEmpty,
                SerializerFeature.WriteNullNumberAsZero,
                SerializerFeature.WriteNullBooleanAsFalse,
                SerializerFeature.WriteDateUseDateFormat,
                //关闭重复引用
                SerializerFeature.DisableCircularReferenceDetect
        );
        return converter;
    }
}

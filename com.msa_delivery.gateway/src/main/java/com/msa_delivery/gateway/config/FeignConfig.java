package com.msa_delivery.gateway.config;

import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

// FeignClient 를 Lazy Load 할 경우 HttpMessageConverters 문제로 아래 config 설정이 필요
@Configuration
public class FeignConfig {
//    @Bean
//    public Decoder feignDecoder() {
//        ObjectFactory<HttpMessageConverters> messageConverters = HttpMessageConverters::new;
//        return new SpringDecoder(messageConverters);
//    }

    @Bean
    public HttpMessageConverters messageConverters() {
        // 필요한 메시지 변환기를 설정
        return new HttpMessageConverters(new MappingJackson2HttpMessageConverter());
    }
}

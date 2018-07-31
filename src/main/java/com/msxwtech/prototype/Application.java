package com.msxwtech.prototype;

import com.msxwtech.prototype.common.exceptions.EnableExceptionHandler;
import com.msxwtech.prototype.common.rest.*;
import com.msxwtech.prototype.common.trace.EnableSleuthTraceFilter;
import com.netflix.hystrix.contrib.javanica.aop.aspectj.HystrixCommandAspect;
import org.modelmapper.ModelMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.netflix.hystrix.EnableHystrix;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Primary;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;


@SpringBootApplication
@EnableWebMvc
@EnableJpaAuditing
@EnableCircuitBreaker
@EnableHystrix
@EnableRetry
@EnableAsync
@EnableFeignClients
@ComponentScan("com.msxwtech.prototype.services")

@EnableExceptionHandler
@EnableRestTemplateLogDetail
@EnableRestTempCommonHeaders
@EnableSleuthTraceFilter
@EnableCustomizeFeignConfiguration

public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    @Primary
    @Order(Ordered.LOWEST_PRECEDENCE)
    public HystrixCommandAspect hystrixAspect() {
        return new HystrixCommandAspect();
    }

    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }


}

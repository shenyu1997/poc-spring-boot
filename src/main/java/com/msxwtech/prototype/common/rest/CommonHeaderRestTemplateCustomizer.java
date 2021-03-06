package com.msxwtech.prototype.common.rest;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateCustomizer;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@Order(10)
public class CommonHeaderRestTemplateCustomizer implements RestTemplateCustomizer {

    @Value("${spring.application.name}")
    private String appName;

    @Value("${foo.svc.hostname}")
    private String hostName;

    @Override
    public void customize(RestTemplate restTemplate) {
        restTemplate.getInterceptors().add(new CommonHeadersInterceptor(appName, hostName));
    }
}

package com.msxwtech.prototype.common.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.client.RestTemplateCustomizer;
import org.springframework.core.annotation.Order;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@Order(100)
public class LoggerRestTemplateCustomizer implements RestTemplateCustomizer {
    private Logger logger = LoggerFactory.getLogger("com.msxwtech.prototype.common.rest");

    @Override
    public void customize(RestTemplate restTemplate) {
        restTemplate.getInterceptors().add((request, body, execution) -> {
            ClientHttpResponse result = null;
            try {
                result = execution.execute(request, body);
                return result;
            } finally {
                logger.debug("rest call >>>> uri:{}, method:{}, request headers:{}, response code:{}, response headers:{}",
                        request.getURI(),
                        request.getMethod(),
                        request.getHeaders(),
                        result == null? null: result.getStatusCode(),
                        result == null? null: result.getHeaders()
                );
            }
        });
    }
}

package com.msxwtech.prototype.common.rest;

import com.netflix.hystrix.exception.HystrixBadRequestException;
import org.springframework.boot.web.client.RestTemplateCustomizer;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.URI;

@Configuration
@Order(Ordered.LOWEST_PRECEDENCE)
public class AutoWrap2HystrixBedRequestExceptionRestTemplateCustomizer implements RestTemplateCustomizer {

    @Override
    public void customize(RestTemplate restTemplate) {
        ResponseErrorHandler errorHandler = restTemplate.getErrorHandler();
        restTemplate.setErrorHandler(new ResponseErrorHandler() {

            @Override
            public boolean hasError(ClientHttpResponse clientHttpResponse) throws IOException {
                return errorHandler.hasError(clientHttpResponse);
            }

            @Override
            public void handleError(ClientHttpResponse clientHttpResponse) throws IOException {
                try {
                    errorHandler.handleError(clientHttpResponse);
                } catch (HttpClientErrorException e) {
                    throw new HystrixBadRequestException(e.getMessage(), e);
                }
            }

            @Override
            public void handleError(URI url, HttpMethod method, ClientHttpResponse response) throws IOException {
                try {
                    errorHandler.handleError(url, method, response);
                } catch (HttpClientErrorException e) {
                    throw new HystrixBadRequestException(e.getMessage(), e);
                }
            }
        });
    }
    
}

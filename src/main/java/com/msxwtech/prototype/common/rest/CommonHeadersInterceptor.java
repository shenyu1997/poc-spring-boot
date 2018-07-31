package com.msxwtech.prototype.common.rest;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

import java.io.IOException;

public class CommonHeadersInterceptor implements ClientHttpRequestInterceptor {
    private String appName;

    private String hostName;

    public CommonHeadersInterceptor(String appName, String hostName) {
        this.appName = appName;
        this.hostName = hostName;
    }

    @Override
    public ClientHttpResponse intercept(HttpRequest httpRequest, byte[] bytes, ClientHttpRequestExecution clientHttpRequestExecution) throws IOException {
        HttpHeaders headers = httpRequest.getHeaders();
        headers.add("service-name",appName);
        headers.add("host-name",hostName);
        return clientHttpRequestExecution.execute(httpRequest, bytes);
    }
}

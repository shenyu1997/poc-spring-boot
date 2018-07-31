package com.msxwtech.prototype.common.rest;

import feign.Client;
import feign.Contract;
import feign.Feign;
import feign.codec.Decoder;
import feign.codec.Encoder;
import feign.codec.ErrorDecoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class FeignBuilderHelper {

    @Autowired
    private Contract contract;

    @Autowired
    private Client client;

    @Autowired
    private Decoder decoder;

    @Autowired
    private Encoder encoder;

    @Value("${spring.application.name}")
    private String appName;

    public <T> T build(Class<T> tClass, String url) {
        return Feign.builder()
                .contract(contract)
                .decoder(decoder)
                .encoder(encoder)
                .client(client)
                .requestInterceptor(requestTemplate -> {
                    requestTemplate.header("service-name", appName);
                })
                .target(tClass, url);
    }
}

package com.msxwtech.prototype.common.rest;

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Import({HttpClientRestTemplateCustomizer.class, HttpClientConfig.class})
public @interface EnableHttpClientRequestFactory {
}

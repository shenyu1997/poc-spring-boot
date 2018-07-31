package com.msxwtech.prototype.common.exceptions;

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Import(ExceptionHandlerAdvice.class)
public @interface EnableExceptionHandler {}

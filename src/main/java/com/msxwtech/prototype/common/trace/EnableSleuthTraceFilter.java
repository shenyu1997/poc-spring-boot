package com.msxwtech.prototype.common.trace;

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Import({SleuthTraceFilter.class})
public @interface EnableSleuthTraceFilter {
}

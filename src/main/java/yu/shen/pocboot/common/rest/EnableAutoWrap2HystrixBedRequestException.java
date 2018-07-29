package yu.shen.pocboot.common.rest;

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Import({AutoWrap2HystrixBedRequestExceptionRestTemplateCustomizer.class})
public @interface EnableAutoWrap2HystrixBedRequestException {
}

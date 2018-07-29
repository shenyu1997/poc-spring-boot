package yu.shen.pocboot.common.rest;

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Import({RetryRestTemplateCustomizer.class})
public @interface EnableRestTemplateRetry {
}

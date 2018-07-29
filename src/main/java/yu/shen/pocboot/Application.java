package yu.shen.pocboot;

import com.netflix.hystrix.contrib.javanica.aop.aspectj.HystrixCommandAspect;
import org.modelmapper.ModelMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.netflix.hystrix.EnableHystrix;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Primary;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import yu.shen.pocboot.common.exceptions.EnableExceptionHandler;
import yu.shen.pocboot.common.rest.EnableAutoWrap2HystrixBedRequestException;
import yu.shen.pocboot.common.rest.EnableRestTemplateRetry;
import yu.shen.pocboot.common.rest.EnableHttpClientRequestFactory;
import yu.shen.pocboot.common.rest.EnableRestTempCommonHeaders;
import yu.shen.pocboot.common.rest.EnableRestTemplateLogDetail;
import yu.shen.pocboot.common.trace.EnableSleuthTraceFilter;


@SpringBootApplication
@EnableWebMvc
@EnableJpaAuditing
@EnableCircuitBreaker
@EnableHystrix
@EnableRetry
@EnableAsync
@ComponentScan("yu.shen.pocboot.services")

@EnableExceptionHandler
@EnableRestTemplateLogDetail
@EnableRestTempCommonHeaders
@EnableHttpClientRequestFactory
@EnableRestTemplateRetry
@EnableAutoWrap2HystrixBedRequestException
@EnableSleuthTraceFilter

public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    @Primary
    @Order(Ordered.LOWEST_PRECEDENCE)
    public HystrixCommandAspect hystrixAspect() {
        return new HystrixCommandAspect();
    }

    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }


}

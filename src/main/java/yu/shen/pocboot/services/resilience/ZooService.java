package yu.shen.pocboot.services.resilience;


import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.exception.HystrixBadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.remoting.RemoteAccessException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.CircuitBreaker;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;

@Service
public class ZooService {
    private Integer count;

    @Retryable(
            include =  { Exception.class },
            exclude = { HystrixBadRequestException.class },
            maxAttemptsExpression  = "#{${rest.retry.max-attempts}}",
            backoff = @Backoff(delayExpression  = "#{${rest.retry.backoff.delay}}", multiplierExpression = "#{${rest.retry.backoff.multiplier}}")
    )
    public void zoo1(int i, RuntimeException e) {
        if (count == null) {
            count = i;
        } else {
            count++;
        }

        switch (count) {
            case 0:
            case 1:
                throw e;
            default:
                return;
        }
    }

    public void reset() {
        this.count = null;
    }

    @CircuitBreaker(
            include  = { RemoteAccessException.class, IOException.class },
            maxAttempts = 2
    )
    public void zoo2(int i, RuntimeException e) {
        System.out.println("=========== in ==============" + i);
        switch (i) {
            case 0:
            case 1:
                throw e;
            default:
                return;
        }
    }

    @HystrixCommand(fallbackMethod = "fallbackMethod")
    public String zoo3(RuntimeException e) {
        System.out.println(">>>>>>>  " + (e != null? e.getMessage(): "null") + "<<<<<<<<" + Thread.currentThread().getId());
        try {
            Thread.sleep(300L);
        } catch (InterruptedException e1) {
            e1.printStackTrace();
        }
        if (e != null) {
            throw e;
        }
        return "ok";
    }

    public String fallbackMethod(RuntimeException e) {
        System.out.println("------ default <----- " + Thread.currentThread().getId());
        return e != null? e.getMessage(): "null";
    }

    @HystrixCommand(fallbackMethod = "fallbackMethod")
    public String zoo4(RuntimeException e) {
        System.out.println(">>>>>>>  " + (e != null? e.getMessage(): "null") + "<<<<<<<<" + Thread.currentThread().getId());
        try {
            Thread.sleep(300L);
        } catch (InterruptedException e1) {
            e1.printStackTrace();
        }
        if (e != null) {
            throw e;
        }
        return "ok";
    }

    private RestTemplate restTemplate;

    @Autowired
    public void setRestTemplateBuilder(RestTemplateBuilder builder) {
        restTemplate = builder.rootUri("http://localhost:9999/abc").build();
    }

    @HystrixCommand(fallbackMethod = "fallbackRestCall")
    public String restCall() {
        return restTemplate.getForObject("/", String.class);
    }

    public String fallbackRestCall() {
        return "default";
    }
}

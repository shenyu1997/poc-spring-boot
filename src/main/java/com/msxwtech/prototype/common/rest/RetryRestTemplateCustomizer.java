package com.msxwtech.prototype.common.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.client.RestTemplateCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.retry.RetryListener;
import org.springframework.retry.backoff.ExponentialBackOffPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Configuration
@Order(1000)
@EnableConfigurationProperties(RetryRestTemplateCustomizer.RestRetryProperties.class)
public class RetryRestTemplateCustomizer implements RestTemplateCustomizer {

    @ConfigurationProperties("rest.retry")
    public static class RestRetryProperties {
        private int maxAttempts = SimpleRetryPolicy.DEFAULT_MAX_ATTEMPTS;
        private BackoffProperties backoff = new BackoffProperties();

        public int getMaxAttempts() {
            return maxAttempts;
        }

        public void setMaxAttempts(int maxAttempts) {
            this.maxAttempts = maxAttempts;
        }

        public BackoffProperties getBackoff() {
            return backoff;
        }

        public void setBackoff(BackoffProperties backoff) {
            this.backoff = backoff;
        }
    }

    public static class BackoffProperties {
        private long delay = ExponentialBackOffPolicy.DEFAULT_INITIAL_INTERVAL;
        private double multiplier = ExponentialBackOffPolicy.DEFAULT_MULTIPLIER;
        private long  maxInterval = ExponentialBackOffPolicy.DEFAULT_MAX_INTERVAL;

        public long getDelay() {
            return delay;
        }

        public void setDelay(long delay) {
            this.delay = delay;
        }

        public double getMultiplier() {
            return multiplier;
        }

        public void setMultiplier(double multiplier) {
            this.multiplier = multiplier;
        }

        public long getMaxInterval() {
            return maxInterval;
        }

        public void setMaxInterval(long maxInterval) {
            this.maxInterval = maxInterval;
        }
    }

    private Logger logger = LoggerFactory.getLogger("com.msxwtech.prototype.common.rest");

    @Autowired
    @Qualifier("forRestTemplate")
    private RetryTemplate retryTemplate;

    @Bean
    @Qualifier("forRestTemplate")
    public RetryTemplate retryTemplate(RetryListener[] listeners, RestRetryProperties restRetryProperties) {
        RetryTemplate retryTemplate = new RetryTemplate();

        ExponentialBackOffPolicy exponentialBackOffPolicy = new ExponentialBackOffPolicy();
        exponentialBackOffPolicy.setInitialInterval(restRetryProperties.getBackoff().getDelay());
        exponentialBackOffPolicy.setMultiplier(restRetryProperties.getBackoff().getMultiplier());
        exponentialBackOffPolicy.setMaxInterval(restRetryProperties.getBackoff().getMaxInterval());
        retryTemplate.setBackOffPolicy(exponentialBackOffPolicy);

        Map<Class<? extends Throwable>, Boolean> retryableExceptions = new HashMap<>();
        retryableExceptions.put(org.apache.http.conn.ConnectTimeoutException.class, true);
        retryableExceptions.put(org.springframework.web.client.ResourceAccessException.class, true);
        retryableExceptions.put(java.net.SocketTimeoutException.class,true);
        SimpleRetryPolicy retryPolicy = new SimpleRetryPolicy(restRetryProperties.getMaxAttempts(), retryableExceptions);
        retryTemplate.setRetryPolicy(retryPolicy);

        retryTemplate.setListeners(listeners);
        return retryTemplate;
    }

    @Override
    public void customize(RestTemplate restTemplate) {
        restTemplate.getInterceptors().add((request, body, execution) -> {
            return retryTemplate.execute(retryContext -> {
                if(retryContext != null && retryContext.getRetryCount() > 0 ) {
                    logger.warn("*** retry rest call *** for {}, method:{}, headers:{}, latest exception:{}", request.getURI(), request.getMethodValue(), request.getHeaders(), retryContext.getLastThrowable().getMessage());
                    logger.debug("latest exception", retryContext.getLastThrowable());
                }
                return execution.execute(request,body);
            });
        });
    }
}

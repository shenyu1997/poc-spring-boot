package com.msxwtech.prototype.services.bar;

import com.msxwtech.prototype.common.pagination.SliceDTO;
import com.msxwtech.prototype.services.foo.FooListedDTO;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.netflix.ribbon.RibbonClient;
import org.springframework.context.annotation.Bean;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.Slice;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.RestTemplate;

@RibbonClient(name = "bar-server")
public class BarRibbonRemoteClient {
    @Bean
    @LoadBalanced
    RestTemplate restTemplate(RestTemplateBuilder restTemplateBuilder) {
        return  restTemplateBuilder.build();
    }


    @Autowired
    RestTemplate restTemplate;

    public Slice<FooListedDTO> findAll() {
        return this.restTemplate.exchange("http://bar-server/foo", HttpMethod.GET, null,new ParameterizedTypeReference<SliceDTO<FooListedDTO>>(){}).getBody();
    }

    @HystrixCommand(fallbackMethod = "pingFallback")
    public String ping() {
        return this.restTemplate.getForObject("http://bar-server/foo/ping", String.class);
    }

    public String pingFallback() {
        return "error";
    }
}

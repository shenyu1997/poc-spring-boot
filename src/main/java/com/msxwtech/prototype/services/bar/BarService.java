package com.msxwtech.prototype.services.bar;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.msxwtech.prototype.common.rest.FeignBuilderHelper;
import com.msxwtech.prototype.services.foo.FooListedDTO;
import feign.Client;
import feign.Contract;
import feign.Feign;
import feign.codec.Decoder;
import feign.codec.Encoder;
import feign.jackson.JacksonDecoder;
import feign.jackson.JacksonEncoder;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.cloud.openfeign.ribbon.LoadBalancerFeignClient;
import org.springframework.data.domain.Slice;
import org.springframework.http.codec.json.Jackson2JsonEncoder;
import org.springframework.stereotype.Service;

@Service
public class BarService {

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private BarRestTempalteRemoteClient barRemoteClient;

    @Autowired
    private BarRibbonRemoteClient barRibbonRemoteClient;

    public BarRestTempalteRemoteClient getBarRemoteClient() {
        return barRemoteClient;
    }

    public void setBarRemoteClient(BarRestTempalteRemoteClient barRemoteClient) {
        this.barRemoteClient = barRemoteClient;
    }

    public Slice<BarListedDTO> findAll() {
        return barRemoteClient.findAll().map(foo -> modelMapper.map(foo, BarListedDTO.class));
    }

    public Slice<BarListedDTO> findAllByRibbon() {
        return barRibbonRemoteClient.findAll().map(foo -> modelMapper.map(foo, BarListedDTO.class));
    }

    public String ping() {
        return barRibbonRemoteClient.ping();
    }

    private BarFeignRemoteClient feignClient;

    @Autowired
    private FeignBuilderHelper feignBuilderHelper;

    @Autowired
    public void setClient() {
        this.feignClient = feignBuilderHelper.build(BarFeignRemoteClient.class, "http://bar-server");
    }

    public String pingByFeign() {
        return feignClient.ping();
    }

    public Slice<BarListedDTO> findAllByFeign() {
        return feignClient.findAll().map(foo -> modelMapper.map(foo, BarListedDTO.class));
    }
}

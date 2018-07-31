package com.msxwtech.prototype.services.bar;


import com.msxwtech.prototype.common.pagination.SliceDTO;
import com.msxwtech.prototype.services.foo.FooListedDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient(value = "bar-server")
public interface BarFeignRemoteClient {
    @RequestMapping(value = "/foo/ping", method = RequestMethod.GET)
    String ping();

    @RequestMapping(value = "/foo/", method = RequestMethod.GET)
    SliceDTO<FooListedDTO> findAll();
}

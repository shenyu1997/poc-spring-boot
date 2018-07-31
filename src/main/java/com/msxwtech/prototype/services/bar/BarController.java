package com.msxwtech.prototype.services.bar;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Slice;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.msxwtech.prototype.services.bar.BarController.URI_ENDPOINT;

@RestController
@RequestMapping(URI_ENDPOINT)
public class BarController {
    private Logger logger = LoggerFactory.getLogger("com.msxwtech.prototype.services.foo");
    public static final String URI_ENDPOINT = "/bar";
    public static final String URI_SINGLE_RESOURCE_ENDPOINT = URI_ENDPOINT + "/{idOrName}";

    @Autowired
    private BarService barService;

    @GetMapping("ping")
    public String ping() {
        return "ok";
    }

    @GetMapping
    public Slice<BarListedDTO> findAll() {
        logger.info("<><><><><><><> bar find all");
        return barService.findAll();
    }
}

package com.msxwtech.prototype.services.bar;

import com.fasterxml.jackson.core.type.TypeReference;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.data.domain.Slice;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import com.msxwtech.prototype.IntegrationTest;
import com.msxwtech.prototype.common.pagination.PageableDTO;
import com.msxwtech.prototype.common.pagination.SliceDTO;
import com.msxwtech.prototype.services.foo.FooListedDTO;
import org.springframework.web.util.NestedServletException;

import java.util.Collections;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


public class BarControllerTest extends IntegrationTest {

    @Rule
    public WireMockRule wireMockRule;

    @Autowired
    public LoadBalancerClient loadBalancerClient;

    @Autowired
    public void configWireMockRule(BarRestTempalteRemoteClient.BarProperties barProperties) {
        wireMockRule =  new WireMockRule(loadBalancerClient.choose("bar-server").getPort());
    }

    /**
     * This test is testing performance of "default requestFactory" and "HttpClient"
     */
    @Autowired
    private RestTemplateBuilder builder;
    @Test
    @Ignore
    public void testBaidu() {
        long begin = System.currentTimeMillis();
        for(int i=0; i<1000; i++) {
            RestTemplate restTemplate = builder.build();
            String page = restTemplate.getForObject("https://www.github.com", String.class);
        }
        System.out.println(">>>>>>>>>>>>>>>" + (System.currentTimeMillis() - begin));
    }

    @Test
    public void findAll() throws Exception{
        FooListedDTO fooListedDTO = new FooListedDTO();
        fooListedDTO.setId(100L);
        fooListedDTO.setName("foo");
        SliceDTO<FooListedDTO> result = new SliceDTO<>(Collections.singletonList(fooListedDTO),PageableDTO.empty(),false);
        stubFor(com.github.tomakehurst.wiremock.client.WireMock.get(urlEqualTo("/foo/"))
                .willReturn(aResponse()
                .withStatus(HttpStatus.OK.value())
                .withHeader("Content-Type", MediaType.APPLICATION_JSON.toString())
                .withBody(objectMapper.writeValueAsString(result))));

        String body = mvc.perform(get(BarController.URI_ENDPOINT))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        Slice<BarListedDTO> returned = objectMapper.readValue(body, new TypeReference<SliceDTO<BarListedDTO>>(){});
        assertThat(returned.getContent(), hasSize(1));
        assertThat(returned.getContent().get(0).getId(), equalTo(fooListedDTO.getId()));
        assertThat(returned.getContent().get(0).getName(), equalTo(fooListedDTO.getName()));


    }

    @Autowired
    private BarRestTempalteRemoteClient barRemoteClient;

    @Test(expected = HttpClientErrorException.class)
    public void test404() {
        stubFor(com.github.tomakehurst.wiremock.client.WireMock.get(urlEqualTo("/foo/yyyy"))
                .willReturn(aResponse()
                        .withStatus(HttpStatus.NOT_FOUND.value())
                        ));
        barRemoteClient.fin404();
    }

    @Test
    public void testTimeoutWithFallBack() {
        stubFor(com.github.tomakehurst.wiremock.client.WireMock.get(urlEqualTo("/foo/"))
                .willReturn(aResponse()
                        .withStatus(HttpStatus.OK.value())
                       .withFixedDelay(5000)));
        assertThat(barRemoteClient.findAll().getContent(), hasSize(0));
    }

    @Test
    public void testRibbon() throws Exception{
        stubFor(com.github.tomakehurst.wiremock.client.WireMock.get(urlEqualTo("/foo/ping"))
                .willReturn(aResponse().withStatus(HttpStatus.OK.value()).withBody("ok!")));
        String result = mvc.perform(get("/bar/ping")).andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
        assertThat(result, equalTo("ok!"));
    }

    @Test
    public void testRibbonWithFallback() throws Exception{
        stubFor(com.github.tomakehurst.wiremock.client.WireMock.get(urlEqualTo("/foo/ping"))
                .willReturn(aResponse().withStatus(HttpStatus.OK.value()).withFixedDelay(5000)));
        String result = mvc.perform(get("/bar/ping")).andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
        assertThat(result, equalTo("error"));
    }

    @Test
    public void testRibbonWith404() throws Exception{
        stubFor(com.github.tomakehurst.wiremock.client.WireMock.get(urlEqualTo("/foo/ping"))
                .willReturn(aResponse().withStatus(HttpStatus.NOT_FOUND.value())));
        mvc.perform(get("/bar/ping")).andExpect(status().isNotFound());
    }

    @Test
    public void testRibbonWith503() throws Exception{
        stubFor(com.github.tomakehurst.wiremock.client.WireMock.get(urlEqualTo("/foo/ping"))
                .willReturn(aResponse().withStatus(503)));
        String result = mvc.perform(get("/bar/ping")).andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
        assertThat(result, equalTo("error"));
    }

    @Test
    public void testRibbonWith500() throws Exception{
        stubFor(com.github.tomakehurst.wiremock.client.WireMock.get(urlEqualTo("/foo/ping"))
                .willReturn(aResponse().withStatus(500)));
        String result = mvc.perform(get("/bar/ping")).andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
        assertThat(result, equalTo("error"));
    }

    @Test
    public void testRibbonWith400() throws Exception{
        stubFor(com.github.tomakehurst.wiremock.client.WireMock.get(urlEqualTo("/foo/ping"))
                .willReturn(aResponse().withStatus(HttpStatus.BAD_REQUEST.value())));
        mvc.perform(get("/bar/ping")).andExpect(status().isBadRequest());
    }

    @Test
    public void testFeign() throws Exception{
        stubFor(com.github.tomakehurst.wiremock.client.WireMock.get(urlEqualTo("/foo/ping"))
                .willReturn(aResponse().withStatus(HttpStatus.OK.value()).withBody("ok")));
        String result = mvc.perform(get("/bar/ping/feign")).andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
        assertThat(result, equalTo("ok"));
    }

    @Test
    public void testFeignSlice() throws Exception{
        FooListedDTO fooListedDTO = new FooListedDTO();
        fooListedDTO.setId(100L);
        fooListedDTO.setName("foo");
        SliceDTO<FooListedDTO> result = new SliceDTO<>(Collections.singletonList(fooListedDTO),PageableDTO.empty(),false);
        stubFor(com.github.tomakehurst.wiremock.client.WireMock.get(urlEqualTo("/foo/"))
                .withHeader("service-name", containing("foo-svc"))
                .willReturn(aResponse()
                        .withStatus(HttpStatus.OK.value())
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON.toString())
                        .withBody(objectMapper.writeValueAsString(result))));

        String body = mvc.perform(get("/bar/feign")).andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
        Slice<BarListedDTO> returned = objectMapper.readValue(body, new TypeReference<SliceDTO<BarListedDTO>>(){});
        assertThat(returned.getContent(), hasSize(1));
        assertThat(returned.getContent().get(0).getId(), equalTo(fooListedDTO.getId()));
        assertThat(returned.getContent().get(0).getName(), equalTo(fooListedDTO.getName()));
    }

    @Test
    public void testFeignTimeout() throws Exception{
        stubFor(com.github.tomakehurst.wiremock.client.WireMock.get(urlEqualTo("/foo/ping"))
                .willReturn(aResponse().withStatus(HttpStatus.OK.value()).withFixedDelay(5000)));
        String result = mvc.perform(get("/bar/ping/feign")).andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
        assertThat(result, equalTo("ok"));
    }
}

package yu.shen.pocboot.services.bar;

import com.fasterxml.jackson.core.type.TypeReference;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.data.domain.Slice;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import yu.shen.pocboot.IntegrationTest;
import yu.shen.pocboot.common.pagination.PageableDTO;
import yu.shen.pocboot.common.pagination.SliceDTO;
import yu.shen.pocboot.services.foo.FooListedDTO;

import java.util.Collections;
import java.util.Optional;

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
    public void configWireMockRule(BarRemoteClient.BarProperties barProperties) {
        wireMockRule =  new WireMockRule(barProperties.getPort());
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
        SliceDTO<FooListedDTO> result = new SliceDTO<>(Collections.singletonList(fooListedDTO),new PageableDTO(Optional.empty(),Optional.empty(), null),false);
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
    private BarRemoteClient barRemoteClient;

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
        assertThat(barRemoteClient.findAll(),equalTo(null));
    }

}

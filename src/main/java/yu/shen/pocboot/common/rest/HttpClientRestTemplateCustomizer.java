package yu.shen.pocboot.common.rest;

import org.apache.http.client.HttpClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateCustomizer;
import org.springframework.core.annotation.Order;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@Order(10)
public class HttpClientRestTemplateCustomizer implements RestTemplateCustomizer {

    @Autowired
    private HttpClient httpClient;

    @Override
    public void customize(RestTemplate restTemplate) {
        HttpComponentsClientHttpRequestFactory clientHttpRequestFactory = new HttpComponentsClientHttpRequestFactory();
        clientHttpRequestFactory.setHttpClient(httpClient);
        restTemplate.setRequestFactory(clientHttpRequestFactory);
    }

}

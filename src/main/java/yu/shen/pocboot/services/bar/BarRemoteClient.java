package yu.shen.pocboot.services.bar;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.Slice;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import yu.shen.pocboot.common.pagination.SliceDTO;
import yu.shen.pocboot.common.rest.HttpConnectionPoolConfigurationProperties;
import yu.shen.pocboot.services.foo.FooListedDTO;

@Service
@EnableConfigurationProperties(BarRemoteClient.BarProperties.class)
public class BarRemoteClient {

    @ConfigurationProperties("bar")
    static class BarProperties extends HttpConnectionPoolConfigurationProperties {
        private String baseURL;

        public String getBaseURL() {
            return baseURL;
        }

        public void setBaseURL(String baseURL) {
            this.baseURL = baseURL;
        }
    }

    private RestTemplate restTemplate;


    public BarRemoteClient(RestTemplateBuilder restTemplateBuilder, BarProperties barProperties) {
        this.restTemplate = restTemplateBuilder.rootUri(UriComponentsBuilder.newInstance()
                .scheme(barProperties.getSchema())
                .host(barProperties.getHost())
                .port(barProperties.getPort())
                .path(barProperties.baseURL).build().toUriString()).additionalCustomizers()
                .build();

    }


    @HystrixCommand(groupKey = "BarRemoteClient", commandKey = "findAll", fallbackMethod = "findAllFallback")
    public Slice<FooListedDTO> findAll() {
            return restTemplate.exchange("/", HttpMethod.GET, null,new ParameterizedTypeReference<SliceDTO<FooListedDTO>>(){}).getBody();
    }

    public Slice<FooListedDTO> findAllFallback() {
        return null;
    }

    @HystrixCommand(groupKey = "BarRemoteClient", commandKey = "fin404", fallbackMethod = "fallback404")
    public String fin404() {
        return restTemplate.getForObject("/yyyy", String.class);
    }

    public String fallback404() {
        return "default";
    }

}

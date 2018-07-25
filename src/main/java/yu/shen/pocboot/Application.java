package yu.shen.pocboot;

import org.modelmapper.ModelMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import yu.shen.pocboot.common.exceptions.EnableExceptionHandler;
import yu.shen.pocboot.common.rest.EnableRestTempCommonHeaders;
import yu.shen.pocboot.common.rest.EnableRestTemplateLogDetail;


@SpringBootApplication
@EnableWebMvc
@EnableExceptionHandler
@EnableRestTemplateLogDetail
@EnableRestTempCommonHeaders
@EnableJpaAuditing
@ComponentScan("yu.shen.pocboot.services")
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }
}

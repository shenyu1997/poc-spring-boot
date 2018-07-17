package yu.shen.pocboot;

import org.modelmapper.ModelMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import yu.shen.pocboot.common.exceptions.EnableExceptionHandler;

import javax.sql.DataSource;


@SpringBootApplication
@EnableWebMvc
@EnableExceptionHandler
@ComponentScan(excludeFilters = @ComponentScan.Filter(type = FilterType.REGEX, pattern="yu.shen.pocboot.common.*"))
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    public ModelMapper modelMapper(DataSource dataSource) {
        return new ModelMapper();
    }
}

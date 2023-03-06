package demo.playground.reactivespringauth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
@ConfigurationPropertiesScan
public class ReactiveSpringAuthApplication {

    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(ReactiveSpringAuthApplication.class, args);
        DataInit dataInit = context.getBean(DataInit.class);
        dataInit.init();
    }

}

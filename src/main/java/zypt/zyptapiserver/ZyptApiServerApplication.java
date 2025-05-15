package zypt.zyptapiserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class ZyptApiServerApplication {
    public static void main(String[] args) {
        SpringApplication.run(ZyptApiServerApplication.class, args);
    }
}
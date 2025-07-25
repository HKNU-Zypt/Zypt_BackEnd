package zypt.zyptapiserver.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import zypt.zyptapiserver.Service.FocusTimeService;
import zypt.zyptapiserver.Service.FocusTimeServiceImpl;
import zypt.zyptapiserver.repository.FocusTimeJdbcRepository;
import zypt.zyptapiserver.repository.FocusTimeJpaRepository;
import zypt.zyptapiserver.repository.FocusTimeMyBatisRepository;
import zypt.zyptapiserver.repository.MemberRepository;

@Configuration
@RequiredArgsConstructor
public class ZyptConfig {

    private final FocusTimeJdbcRepository focusTimeJdbcRepository;
    private final FocusTimeMyBatisRepository focusTimeMyBatisRepository;
    private final FocusTimeJpaRepository focusTimeJpaRepository;
    private final MemberRepository memberRepository;

    @Bean
    public FocusTimeService focusTimeService() {
        return new FocusTimeServiceImpl(focusTimeMyBatisRepository, memberRepository);
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    // swagger
    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .components(new Components())
                .info(apiInfo());
    }

    private Info apiInfo() {
        return new Info()
                .title("Zypt Swagger")
                .description("Zypt 유저 인증, 인가, REST API 정보")
                .version("1.0.0");
    }
}

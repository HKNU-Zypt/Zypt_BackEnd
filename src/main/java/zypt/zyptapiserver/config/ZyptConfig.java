package zypt.zyptapiserver.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import zypt.zyptapiserver.repository.Member.MemberRepository;
import zypt.zyptapiserver.repository.focustime.FocusTimeJdbcRepository;
import zypt.zyptapiserver.repository.focustime.FocusTimeJpaRepository;
import zypt.zyptapiserver.repository.focustime.FocusTimeMyBatisRepository;
import zypt.zyptapiserver.repository.focustime.FocusTimeStatisticRepository;
import zypt.zyptapiserver.service.focustime.FocusTimeService;
import zypt.zyptapiserver.service.focustime.FocusTimeServiceImpl;
import zypt.zyptapiserver.service.focustime.FocusTimeStatisticsServiceImpl;
import zypt.zyptapiserver.service.focustime.FocusTimeStatisticsService;

@Configuration
@RequiredArgsConstructor
public class ZyptConfig {

    private final FocusTimeJdbcRepository focusTimeJdbcRepository;
    private final FocusTimeMyBatisRepository focusTimeMyBatisRepository;
    private final FocusTimeJpaRepository focusTimeJpaRepository;

    private final FocusTimeStatisticRepository focusTimeStatisticRepository;
    private final MemberRepository memberRepository;


    @Bean
    public FocusTimeService focusTimeService() {
        return new FocusTimeServiceImpl(focusTimeJpaRepository, memberRepository);
    }

    @Bean
    public FocusTimeStatisticsService focusTimeStatisticsService() {
        return new FocusTimeStatisticsServiceImpl(focusTimeStatisticRepository);
    }


    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    // swagger
    @Bean
    public OpenAPI openAPI() {
        SecurityScheme securityScheme = new SecurityScheme()
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT")
                .in(SecurityScheme.In.HEADER)
                .name("Authorization");

        SecurityRequirement securityRequirement = new SecurityRequirement().addList("BearerAuth");

        return new OpenAPI()
                .info(new Info().title("Zypt API")
                        .description("Zypt Application API Documentation")
                        .version("v1.0"))
                .addSecurityItem(securityRequirement)
                .schemaRequirement("BearerAuth", securityScheme);

    }

}

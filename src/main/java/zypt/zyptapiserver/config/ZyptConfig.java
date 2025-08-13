package zypt.zyptapiserver.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.web.client.RestTemplate;
import zypt.zyptapiserver.Service.FocusTimeService;
import zypt.zyptapiserver.Service.FocusTimeServiceImpl;
import zypt.zyptapiserver.Service.FocusTimeServiceImplV2;
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
    @Primary
    public FocusTimeService focusTimeService() {
        return new FocusTimeServiceImpl(focusTimeJpaRepository, memberRepository);
    }

    @Bean
    @Profile("test")
    public FocusTimeService focusTimeServiceImplV2() {
        return new FocusTimeServiceImplV2(focusTimeJpaRepository, memberRepository);
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

package zypt.zyptapiserver.config;


import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import zypt.zyptapiserver.repository.mapper.FocusMapper;
import zypt.zyptapiserver.repository.focustime.FocusTimeMyBatisRepository;

@Configuration
@RequiredArgsConstructor
public class  MyBatisConfig {

    private final FocusMapper focusMapper;

    @Bean
    public FocusTimeMyBatisRepository focusTimeMyBatisRepository() {
        return new FocusTimeMyBatisRepository(focusMapper);
    }
}

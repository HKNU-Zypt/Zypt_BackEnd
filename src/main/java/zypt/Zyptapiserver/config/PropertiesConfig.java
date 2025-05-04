package zypt.Zyptapiserver.config;

import zypt.Zyptapiserver.livekit.LiveKitSource;
import zypt.Zyptapiserver.util.JwtUtils;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties({LiveKitSource.class, JwtUtils.class})
public class PropertiesConfig {

}

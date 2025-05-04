package zypt.zyptapiserver.config;

import zypt.zyptapiserver.livekit.LiveKitSource;
import zypt.zyptapiserver.util.JwtUtils;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties({LiveKitSource.class, JwtUtils.class})
public class PropertiesConfig {

}

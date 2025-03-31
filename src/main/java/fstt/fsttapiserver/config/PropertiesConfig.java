package fstt.fsttapiserver.config;

import fstt.fsttapiserver.livekit.LiveKitSource;
import fstt.fsttapiserver.util.JwtUtils;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties({LiveKitSource.class, JwtUtils.class})
public class PropertiesConfig {

}

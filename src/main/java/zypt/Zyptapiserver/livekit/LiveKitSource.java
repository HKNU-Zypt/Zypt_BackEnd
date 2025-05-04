package zypt.Zyptapiserver.livekit;

import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@ConfigurationProperties("livekit")
public class LiveKitSource {

    private String API_KEY;
    private String SECRET_KEY;
    private String URL;

    private LiveKitSource(String API_KEY, String SECRET_KEY, String URL) {
        this.API_KEY = API_KEY;
        this.SECRET_KEY = SECRET_KEY;
        this.URL = URL;
    }
}

package fstt.fsttapiserver;

import fstt.fsttapiserver.livekit.LiveKitSource;
import io.livekit.server.RoomServiceClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class FsttApiServerApplication {



    public static void main(String[] args) {
        SpringApplication.run(FsttApiServerApplication.class, args);


    }

}

package zypt.zyptapiserver.util;


import com.fasterxml.uuid.Generators;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Slf4j
public class Uuidv7Test {



    @Test
    void run() {

        String t0 = Generators.timeBasedEpochGenerator().generate().toString();
        String t1 = Generators.timeBasedEpochRandomGenerator().generate().toString();

        log.info(t0);
        log.info(t1);
    }

}

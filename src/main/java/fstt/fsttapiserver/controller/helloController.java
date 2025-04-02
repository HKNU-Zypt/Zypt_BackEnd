package fstt.fsttapiserver.controller;

import lombok.Getter;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class helloController {


    @GetMapping("/a")
    public String hello() {
        return "hello";
    }
}

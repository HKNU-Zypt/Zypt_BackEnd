package fstt.fsttapiserver.controller;

import fstt.fsttapiserver.livekit.LiveKitService;
import fstt.fsttapiserver.livekit.LiveKitSource;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
public class helloController {

    private final LiveKitService service;

    @GetMapping("/a")
    public String hello() {
        return "hello";
    }

    @GetMapping("/createRoom")
    public ResponseEntity<String> createRoom(@RequestParam("name") String name, @RequestParam("userid") String userid, @RequestParam("roomName") String roomName) throws IOException {
        return ResponseEntity.ok(service.createRoom(name, userid, roomName));
    }

    @GetMapping("/joinRoom")
    public ResponseEntity<String> joinRoom(@RequestParam("name") String name, @RequestParam("userid") String userid, @RequestParam("roomName") String roomName) {
        return ResponseEntity.ok(service.getLiveKitAccessToken(name, userid, roomName));
    }
}

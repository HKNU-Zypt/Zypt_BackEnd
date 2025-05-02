package fstt.fsttapiserver.controller;

import fstt.fsttapiserver.auth.user.CustomUserDetails;
import fstt.fsttapiserver.livekit.LiveKitService;
import fstt.fsttapiserver.livekit.dto.LiveKitAccessTokenDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
public class LiveKitController {

    private final LiveKitService service;

    // 인가 테스트
    @GetMapping("/a")
    public String hello() {
        return "hello";
    }

    @GetMapping("/createRoom")
    public ResponseEntity<LiveKitAccessTokenDTO> createRoom(@RequestParam("nickName") String nickName,
                                                            @AuthenticationPrincipal CustomUserDetails userDetails,
                                                            @RequestParam("roomName") String roomName) throws IOException {
        return ResponseEntity.ok(service.createRoom(nickName, userDetails.getUsername(), roomName));
    }

    @GetMapping("/joinRoom")
    public ResponseEntity<LiveKitAccessTokenDTO> joinRoom(@RequestParam("nickName") String nickName,
                                           @AuthenticationPrincipal CustomUserDetails userDetails,
                                           @RequestParam("roomName") String roomName) {
        return ResponseEntity.ok(service.getLiveKitAccessToken(nickName, userDetails.getUsername(), roomName));
    }
}

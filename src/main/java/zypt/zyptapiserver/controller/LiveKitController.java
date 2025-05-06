package zypt.zyptapiserver.controller;

import livekit.LivekitModels;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.web.bind.annotation.*;
import zypt.zyptapiserver.auth.user.CustomUserDetails;
import zypt.zyptapiserver.livekit.LiveKitService;
import zypt.zyptapiserver.livekit.dto.LiveKitAccessTokenDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import zypt.zyptapiserver.livekit.dto.LiveKitParticipantDTO;
import zypt.zyptapiserver.livekit.dto.LiveKitRoomDTO;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/room")
@RequiredArgsConstructor
public class LiveKitController {

    private final LiveKitService service;

    @GetMapping("/create")
    public ResponseEntity<LiveKitAccessTokenDTO> createRoom(@AuthenticationPrincipal CustomUserDetails userDetails,
                                                            @RequestParam("roomName") String roomName,
                                                            @RequestParam(name = "maxParticipant", defaultValue = "10") int maxParticipant) throws IOException {
        return ResponseEntity.ok(service.createRoom(userDetails.getNickName(), userDetails.getUsername(), roomName, maxParticipant));
    }

    @GetMapping("/join")
    public ResponseEntity<LiveKitAccessTokenDTO> joinRoom(@RequestParam("nickName") String nickName,
                                           @AuthenticationPrincipal CustomUserDetails userDetails,
                                           @RequestParam("roomName") String roomName) {
        return ResponseEntity.ok(service.getLiveKitAccessToken(nickName, userDetails.getUsername(), roomName));
    }

    @GetMapping("/{roomName}/participant")
    public ResponseEntity<List<LiveKitParticipantDTO>> findParticipantByRoomName(@PathVariable("roomName") String roomName) {
        return ResponseEntity.ok(service.getRoomParticipantsByRoomName(roomName));
    }

    @GetMapping("")
    public ResponseEntity<List<LiveKitRoomDTO>> findAllRooms() {
        return ResponseEntity.ok(service.findAllRooms());
    }

    @GetMapping("/delete")
    public ResponseEntity<Boolean> deleteRoomByRoomName(@RequestParam("roomName") String roomName) {
        return ResponseEntity.ok(service.deleteRoom(roomName));
    }
}

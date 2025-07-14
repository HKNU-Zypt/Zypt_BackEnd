package zypt.zyptapiserver.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
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
//@Tag(name = "LiveKit API", description = "LiveKit 방 생성, 참여 등 API")
@RequestMapping("/api/rooms")
@RequiredArgsConstructor
public class LiveKitController {

    private final LiveKitService service;

    @PostMapping("/create")
    public ResponseEntity<LiveKitAccessTokenDTO> createRoom(@AuthenticationPrincipal CustomUserDetails userDetails,
                                                            @RequestParam("roomName") String roomName,
                                                            @RequestParam(name = "maxParticipant", required = false, defaultValue = "10") int maxParticipant) throws IOException {
        LiveKitAccessTokenDTO liveKitAccessTokenDTO = service.createRoom(userDetails.getNickName(), userDetails.getUsername(), roomName, maxParticipant);
        return ResponseEntity.ok(liveKitAccessTokenDTO);
    }

    @PostMapping("/{roomName}")
    public ResponseEntity<LiveKitAccessTokenDTO> joinRoom(@AuthenticationPrincipal CustomUserDetails userDetails,
                                                          @PathVariable("roomName") String roomName) {
        LiveKitAccessTokenDTO liveKitAccessTokenDTO = service.getLiveKitAccessToken(userDetails.getNickName(), userDetails.getUsername(), roomName);
        return ResponseEntity.ok(liveKitAccessTokenDTO);
    }

    @GetMapping("/{roomName}/participant")
    public ResponseEntity<List<LiveKitParticipantDTO>> findParticipantByRoomName(@PathVariable("roomName") String roomName) {
        List<LiveKitParticipantDTO> participantsDto = service.getRoomParticipantsByRoomName(roomName);
        return ResponseEntity.ok(participantsDto);
    }

    @GetMapping("")
    public ResponseEntity<List<LiveKitRoomDTO>> findAllRooms() {
        List<LiveKitRoomDTO> allRooms = service.findAllRooms();
        return ResponseEntity.ok(allRooms);
    }

    @DeleteMapping("/{roomName}")
    public ResponseEntity<String> deleteRoomByRoomName(@PathVariable("roomName") String roomName) {
        service.deleteRoom(roomName);

        return ResponseEntity.ok(roomName + " 방을 삭제하였습니다.");
    }

}

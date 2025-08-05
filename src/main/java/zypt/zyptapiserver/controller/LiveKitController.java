package zypt.zyptapiserver.controller;

import io.swagger.v3.oas.annotations.Operation;
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
@Tag(name = "LiveKit API", description = "LiveKit 방 생성, 참여 등 API")
@RequestMapping("/api/rooms")
@RequiredArgsConstructor
public class LiveKitController {

    private final LiveKitService service;

    @PostMapping("/create")
    @Operation(summary = "룸 생성", description = "액세스토큰 해더 필수, \n\n 방이름을 통해 새로운 룸 생성 응답은 방 액세스 토큰")
    public ResponseEntity<LiveKitAccessTokenDTO> createRoom(@AuthenticationPrincipal CustomUserDetails userDetails,
                                                            @RequestParam("roomName") String roomName,
                                                            @RequestParam(name = "maxParticipant", required = false, defaultValue = "10") int maxParticipant) throws IOException {
        LiveKitAccessTokenDTO liveKitAccessTokenDTO = service.createRoom(userDetails.getNickName(), userDetails.getUsername(), roomName, maxParticipant);
        return ResponseEntity.ok(liveKitAccessTokenDTO);
    }

    @PostMapping("/{roomName}")
    @Operation(summary = "룸 참여", description = "액세스토큰 해더 필수, \n\n api/rooms/방이름 으로 접속")
    public ResponseEntity<LiveKitAccessTokenDTO> joinRoom(@AuthenticationPrincipal CustomUserDetails userDetails,
                                                          @PathVariable("roomName") String roomName) {
        LiveKitAccessTokenDTO liveKitAccessTokenDTO = service.getLiveKitAccessToken(userDetails.getNickName(), userDetails.getUsername(), roomName);
        return ResponseEntity.ok(liveKitAccessTokenDTO);
    }

    @GetMapping("/{roomName}/participant")
    @Operation(summary = "룸 참가자 조회", description = "액세스토큰 해더 필수, \n\n api/rooms/방이름/participant 로 조회")
    public ResponseEntity<List<LiveKitParticipantDTO>> findParticipantByRoomName(@PathVariable("roomName") String roomName) {
        List<LiveKitParticipantDTO> participantsDto = service.getRoomParticipantsByRoomName(roomName);
        return ResponseEntity.ok(participantsDto);
    }

    @GetMapping("")
    @Operation(summary = "전체 룸 정보 조회", description = "액세스토큰 해더 필수, \n\n 모든 룸 조회")
    public ResponseEntity<List<LiveKitRoomDTO>> findAllRooms() {
        List<LiveKitRoomDTO> allRooms = service.findAllRooms();
        return ResponseEntity.ok(allRooms);
    }

    @DeleteMapping("/{roomName}")
    @Operation(summary = "룸 삭제", description = "액세스토큰 해더 필수, \n\n roomName에 해당하는 룸 삭제")
    public ResponseEntity<String> deleteRoomByRoomName(@PathVariable("roomName") String roomName) {
        service.deleteRoom(roomName);

        return ResponseEntity.ok(roomName + " 방을 삭제하였습니다.");
    }

}

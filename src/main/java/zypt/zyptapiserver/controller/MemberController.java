package zypt.zyptapiserver.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import zypt.zyptapiserver.Service.MemberService;
import zypt.zyptapiserver.domain.dto.SignUpMemberInfoDto;

@RestController
@RequestMapping("/member")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @PostMapping("/signup")
    public ResponseEntity<Void> updateMemberNickName(@RequestBody SignUpMemberInfoDto signUpMemberInfoDto) {
        memberService.updateNickName(signUpMemberInfoDto.id(), signUpMemberInfoDto.nickName());
        return ResponseEntity.noContent().build();
    }
}

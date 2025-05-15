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

    // 토큰 안에 있는 정보를 활용? 아니면 따로 id 값을 전송해서 처리 ?
    // 문제점 1. 닉네임을 업데이트할 경우 기존 발급한 토큰에 있는 닉네임은 ?


    @PostMapping("/signup")
    public ResponseEntity<Void> updateMemberNickName(@RequestBody SignUpMemberInfoDto signUpMemberInfoDto) {
        memberService.updateNickName(signUpMemberInfoDto.id(), signUpMemberInfoDto.nickName());
        return ResponseEntity.noContent().build();
    }
}

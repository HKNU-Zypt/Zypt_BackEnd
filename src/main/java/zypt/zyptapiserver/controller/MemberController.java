package zypt.zyptapiserver.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import zypt.zyptapiserver.Service.MemberService;

@RestController
@RequestMapping("/member")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @PostMapping("/{id}")
    public ResponseEntity<Void> updateMemberNickName(@PathVariable("id") String id, @RequestParam("nickName") String nickName) {
        memberService.updateNickName(id, nickName);
        return ResponseEntity.noContent().build();
    }
}

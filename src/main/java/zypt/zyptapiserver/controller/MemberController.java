package zypt.zyptapiserver.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import zypt.zyptapiserver.Service.MemberService;
import zypt.zyptapiserver.domain.Member;
import zypt.zyptapiserver.repository.MemberRepository;

@RestController
@RequestMapping("/member")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @PostMapping("/{id}")
    public ResponseEntity<Void> updateMemberNickName(@PathVariable("id") String id, @RequestParam("nickName") String nickName) {

        return ResponseEntity.noContent().build();
    }
}

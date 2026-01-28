package com.kdt03.fashion_api.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kdt03.fashion_api.domain.dto.MemberDTO;
import com.kdt03.fashion_api.domain.dto.MemberLoginDTO;
import com.kdt03.fashion_api.service.MemberService;

import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.kdt03.fashion_api.domain.dto.MemberSignupDTO;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/members")
public class MemberController {
    private final MemberService memberService;

    @GetMapping("/list")
    public List<MemberDTO> getMembers() {
        return memberService.getAllMembers();
    }

    @PostMapping("/signup")
    public ResponseEntity<String> signup(@RequestBody MemberSignupDTO dto) {
        // TODO: process POST request
        memberService.signup(dto);
        return ResponseEntity.ok("회원가입 성공");

    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody MemberLoginDTO dto) {
       
        try {
            String message = memberService.login(dto);

            return ResponseEntity.ok(message);

        } catch (RuntimeException e) {
            return ResponseEntity.status(401).body(e.getMessage());
        }
    }

}
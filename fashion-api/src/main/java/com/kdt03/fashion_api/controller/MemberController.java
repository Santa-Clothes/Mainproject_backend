package com.kdt03.fashion_api.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kdt03.fashion_api.domain.dto.MemberLoginDTO;
import com.kdt03.fashion_api.service.MemberService;
import com.kdt03.fashion_api.util.JWTUtil;

import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.kdt03.fashion_api.domain.Member;
import com.kdt03.fashion_api.domain.dto.MemberSignupDTO;
import com.kdt03.fashion_api.repository.MemberRepository;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/members")
public class MemberController {
    private final MemberService memberService;
    private final MemberRepository memberRepo;
    private final JWTUtil jwtUtil;
    
    @PostMapping("/signup")
    public ResponseEntity<String> signup(@RequestBody MemberSignupDTO dto) {
        // TODO: process POST request
        memberService.signup(dto);
        return ResponseEntity.ok("회원가입 성공");

    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody MemberLoginDTO dto) {
        try {
            memberService.login(dto);
            String token = jwtUtil.getJWT(dto.getId());
            

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("accessToken", token);
            response.put("userId", dto.getId());
            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {

            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", e.getMessage());

            return ResponseEntity.status(401).body(errorResponse);
        }
    }

}
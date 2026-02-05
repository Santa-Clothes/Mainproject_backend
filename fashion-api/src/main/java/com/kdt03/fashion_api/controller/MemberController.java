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

import org.springframework.web.bind.annotation.GetMapping;
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

    @GetMapping("/list")
    public ResponseEntity<java.util.List<com.kdt03.fashion_api.domain.dto.MemberResponseDTO>> getAllMembers() {
        return ResponseEntity.ok(memberService.getAllMembers());
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody MemberLoginDTO dto) {
        try {
            Member member = memberService.login(dto);
            memberService.login(dto);
            String token = jwtUtil.getJWT(dto.getId());

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("accessToken", token);
            response.put("userId", dto.getId());
            String profile = member.getProfile();
            String profilepath;
            if (profile == null || profile.isEmpty()) {
                profilepath = "http://10.125.121.182:8080/uploads/profiles/default-avatar.png";
            } else {
                profilepath = "http://10.125.121.182:8080" + profile;
            }
            response.put("profile", profilepath);
            response.put("name", member.getNickname());
            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {

            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", e.getMessage());

            return ResponseEntity.status(401).body(errorResponse);
        }
    }

    @io.swagger.v3.oas.annotations.Operation(description = "서버 세션을 무효화하고 로그아웃 처리.")
    @PostMapping("/logout")
    public void logout() {
        // SecurityConfig에서 처리
    }

}
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
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.kdt03.fashion_api.domain.Member;
import com.kdt03.fashion_api.domain.dto.MemberSignupDTO;
import com.kdt03.fashion_api.repository.MemberRepository;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "회원 관리 (Member)", description = "회원 가입, 로그인, 정보 조회 및 탈퇴 관련 API")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/members")
public class MemberController {
    private final MemberService memberService;
    private final MemberRepository memberRepo;
    private final JWTUtil jwtUtil;

    @Operation(summary = "회원 가입", description = "신규 회원 정보를 등록합니다. 아이디, 비밀번호, 닉네임이 필요합니다.")
    @PostMapping("/signup")
    public ResponseEntity<String> signup(@RequestBody MemberSignupDTO dto) {
        memberService.signup(dto);
        return ResponseEntity.ok("회원가입 성공");
    }

    @Operation(summary = "회원 목록 조회", description = "관리자용 기능으로 등록된 모든 회원 목록을 조회합니다.")
    @GetMapping("/list")
    public ResponseEntity<java.util.List<com.kdt03.fashion_api.domain.dto.MemberResponseDTO>> getAllMembers() {
        return ResponseEntity.ok(memberService.getAllMembers());
    }

    @Operation(summary = "로그인", description = "아이디와 비밀번호로 인증하고 JWT 액세스 토큰을 발급받습니다.")
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody MemberLoginDTO dto) {
        try {
            Member member = memberService.login(dto);
            String token = jwtUtil.getJWT(dto.getId());

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("accessToken", token);
            response.put("userId", dto.getId());
            String profile = member.getProfile();
            String profilepath;
            if (profile == null || profile.isEmpty()) {
                profilepath = "/uploads/profiles/default-avatar.png";
            } else {
                profilepath = profile;
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

    @Operation(summary = "로그아웃", description = "서버 세션을 무효화하고 로그아웃 처리합니다. (실제 처리는 SecurityConfig에서 수행)")
    @PostMapping("/logout")
    public void logout() {
        // SecurityConfig에서 처리
    }

    @Operation(summary = "회원 탈퇴", description = "현재 로그인한 사용자의 계정을 삭제(탈퇴) 처리합니다.")
    @DeleteMapping("/withdraw")
    public ResponseEntity<?> withdraw(
            @Parameter(description = "비밀번호 확인을 위한 정보") @RequestBody MemberLoginDTO dto,
            java.security.Principal principal) {
        if (principal == null) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "로그인이 필요합니다.");
            return ResponseEntity.status(401).body(errorResponse);
        }
        try {
            memberService.withdraw(principal.getName(), dto.getPassword());
            return ResponseEntity.ok("회원탈퇴 성공");
        } catch (RuntimeException e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", e.getMessage());

            return ResponseEntity.status(401).body(errorResponse);
        }
    }

    @Operation(summary = "내 정보 조회", description = "JWT 토큰을 통해 현재 로그인한 사용자의 정보를 조회합니다.")
    @GetMapping("/me")
    public ResponseEntity<?> getMyInfo(java.security.Principal principal) {
        if (principal == null) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "로그인이 필요합니다.");
            return ResponseEntity.status(401).body(errorResponse);
        }

        return memberRepo.findById(principal.getName())
                .map(member -> {
                    Map<String, Object> response = new HashMap<>();
                    response.put("success", true);
                    response.put("userId", member.getId());
                    response.put("name", member.getNickname());

                    String profile = member.getProfile();
                    String profilepath;
                    if (profile == null || profile.isEmpty()) {
                        profilepath = "/uploads/profiles/default-avatar.png";
                    } else {
                        profilepath = profile;
                    }
                    response.put("profile", profilepath);
                    return ResponseEntity.ok(response);
                })
                .orElseGet(() -> {
                    Map<String, Object> errorResponse = new HashMap<>();
                    errorResponse.put("success", false);
                    errorResponse.put("message", "사용자를 찾을 수 없습니다.");
                    return ResponseEntity.status(404).body(errorResponse);
                });
    }
}

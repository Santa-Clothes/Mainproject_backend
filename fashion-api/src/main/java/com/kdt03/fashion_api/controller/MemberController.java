package com.kdt03.fashion_api.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kdt03.fashion_api.domain.Member;
import com.kdt03.fashion_api.domain.dto.MemberLoginDTO;
import com.kdt03.fashion_api.domain.dto.MemberResponseDTO;
import com.kdt03.fashion_api.domain.dto.MemberSignupDTO;
import com.kdt03.fashion_api.domain.dto.MemberUpdateDTO;
import com.kdt03.fashion_api.repository.MemberRepository;
import com.kdt03.fashion_api.service.MemberService;
import com.kdt03.fashion_api.util.JWTUtil;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "회원 관리 (Member)", description = "회원 가입, 로그인, 정보 조회, 수정 및 탈퇴 관련 API")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/members")
public class MemberController {
    private final MemberService memberService;
    private final MemberRepository memberRepo;
    private final JWTUtil jwtUtil;
    private final com.kdt03.fashion_api.service.ImageUploadService imageUploadService;

    @Operation(summary = "회원 가입", description = "신규 회원 정보를 등록합니다. 아이디, 비밀번호, 닉네임이 필요하며, 프로필 사진은 선택사항입니다.")
    @io.swagger.v3.oas.annotations.responses.ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "회원가입 성공", content = @io.swagger.v3.oas.annotations.media.Content(mediaType = "text/plain", examples = @io.swagger.v3.oas.annotations.media.ExampleObject(value = "회원가입 성공"))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "서버 오류", content = @io.swagger.v3.oas.annotations.media.Content(mediaType = "text/plain", examples = @io.swagger.v3.oas.annotations.media.ExampleObject(value = "서버 오류: ...")))
    })
    @PostMapping(value = "/signup", consumes = org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> signup(
            @Parameter(description = "회원 아이디", required = true) @org.springframework.web.bind.annotation.RequestParam("id") String id,
            @Parameter(description = "회원 비밀번호", required = true) @org.springframework.web.bind.annotation.RequestParam("password") String password,
            @Parameter(description = "회원 닉네임", required = true) @org.springframework.web.bind.annotation.RequestParam("nickname") String nickname,
            @Parameter(description = "프로필 이미지 파일 (선택사항, 파라미터명: file)", required = false) @org.springframework.web.bind.annotation.RequestParam(value = "file", required = false) org.springframework.web.multipart.MultipartFile profileImage) {

        System.out.println("회원가입 요청: id=" + id + ", nickname=" + nickname);
        if (profileImage != null && !profileImage.isEmpty()) {
            System.out
                    .println("프로필 이미지 수신됨: " + profileImage.getOriginalFilename() + ", size=" + profileImage.getSize());
        } else {
            System.out.println("프로필 이미지 없음 (null 또는 empty) - 파라미터명 'file' 확인 필요");
        }

        MemberSignupDTO dto = MemberSignupDTO.builder()
                .id(id)
                .password(password)
                .nickname(nickname)
                .build();

        memberService.signup(dto, profileImage);
        return ResponseEntity.ok("회원가입 성공");
    }

    @Operation(summary = "프로필 이미지 업로드", description = "회원의 프로필 이미지를 Supabase 버킷에 업로드하고 회원 정보를 업데이트합니다.")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "업로드 성공", content = @io.swagger.v3.oas.annotations.media.Content(mediaType = "application/json", examples = @io.swagger.v3.oas.annotations.media.ExampleObject(value = "{\"success\": true, \"imageUrl\": \"http://...\", \"message\": \"업로드 성공\"}")))
    @PostMapping(value = "/profile", consumes = org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadProfile(
            @Parameter(description = "업로드할 프로필 이미지 파일", required = true) @org.springframework.web.bind.annotation.RequestParam("file") org.springframework.web.multipart.MultipartFile file,
            java.security.Principal principal) {
        Map<String, Object> response = new HashMap<>();

        if (principal == null) {
            response.put("success", false);
            response.put("message", "로그인이 필요합니다.");
            return ResponseEntity.status(401).body(response);
        }

        try {
            String imageUrl = imageUploadService.uploadProfileImage(file, principal.getName());

            response.put("success", true);
            response.put("imageUrl", imageUrl);
            response.put("message", "업로드 성공");

            return ResponseEntity.ok(response);

        } catch (java.io.IOException e) {
            response.put("success", false);
            response.put("message", "서버 오류: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    @Operation(summary = "회원 목록 조회", description = "관리자용 기능으로 등록된 모든 회원 목록을 조회합니다.")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "조회 성공", content = @io.swagger.v3.oas.annotations.media.Content(mediaType = "application/json", examples = @io.swagger.v3.oas.annotations.media.ExampleObject(value = "[{\"id\": \"user1\", \"nickname\": \"홍길동\", \"provider\": \"local\", \"profile\": \"/uploads/profiles/user1...\"}]")))
    @GetMapping("/list")
    public ResponseEntity<List<MemberResponseDTO>> getAllMembers() {
        return ResponseEntity.ok(memberService.getAllMembers());
    }

    @Operation(summary = "로그인", description = "아이디와 비밀번호로 인증하고 JWT 액세스 토큰을 발급받습니다.")
    @io.swagger.v3.oas.annotations.responses.ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "로그인 성공", content = @io.swagger.v3.oas.annotations.media.Content(mediaType = "application/json", examples = @io.swagger.v3.oas.annotations.media.ExampleObject(value = "{\"success\": true, \"accessToken\": \"ey...\", \"userId\": \"user1\", \"profile\": \"...\", \"name\": \"홍길동\"}"))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "로그인 실패", content = @io.swagger.v3.oas.annotations.media.Content(mediaType = "application/json", examples = @io.swagger.v3.oas.annotations.media.ExampleObject(value = "{\"success\": false, \"message\": \"비밀번호가 다릅니다.\"}")))
    })
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
            String profilepath = (profile == null || profile.isEmpty())
                    ? "/uploads/profiles/default-avatar.png"
                    : profile;

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
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "로그아웃 성공")
    @PostMapping("/logout")
    public void logout() {
        // SecurityConfig에서 처리
    }

    @Operation(summary = "회원 탈퇴", description = "현재 로그인한 사용자의 계정을 삭제(탈퇴) 처리합니다.")
    @io.swagger.v3.oas.annotations.responses.ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "탈퇴 성공", content = @io.swagger.v3.oas.annotations.media.Content(mediaType = "text/plain", examples = @io.swagger.v3.oas.annotations.media.ExampleObject(value = "회원탈퇴 성공"))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "인증 실패", content = @io.swagger.v3.oas.annotations.media.Content(mediaType = "application/json", examples = @io.swagger.v3.oas.annotations.media.ExampleObject(value = "{\"success\": false, \"message\": \"비밀번호가 다릅니다.\"}")))
    })
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
    @io.swagger.v3.oas.annotations.responses.ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "조회 성공", content = @io.swagger.v3.oas.annotations.media.Content(mediaType = "application/json", examples = @io.swagger.v3.oas.annotations.media.ExampleObject(value = "{\"success\": true, \"userId\": \"user1\", \"name\": \"홍길동\", \"profile\": \"...\"}"))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "인증 필요", content = @io.swagger.v3.oas.annotations.media.Content(mediaType = "application/json", examples = @io.swagger.v3.oas.annotations.media.ExampleObject(value = "{\"success\": false, \"message\": \"로그인이 필요합니다.\"}")))
    })
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
                    String profilepath = (profile == null || profile.isEmpty())
                            ? "/uploads/profiles/default-avatar.png"
                            : profile;

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

    @Operation(summary = "회원 정보 수정", description = "로그인한 사용자의 닉네임 또는 비밀번호를 수정합니다. 값이 있는 필드만 변경됩니다.")
    @io.swagger.v3.oas.annotations.responses.ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "수정 성공", content = @io.swagger.v3.oas.annotations.media.Content(mediaType = "application/json", examples = @io.swagger.v3.oas.annotations.media.ExampleObject(value = "{\"success\": true, \"message\": \"회원 정보가 성공적으로 수정되었습니다.\"}"))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "인증 필요", content = @io.swagger.v3.oas.annotations.media.Content(mediaType = "application/json", examples = @io.swagger.v3.oas.annotations.media.ExampleObject(value = "{\"success\": false, \"message\": \"로그인이 필요합니다.\"}")))
    })
    @PatchMapping("/update")
    public ResponseEntity<?> updateMember(
            @RequestBody MemberUpdateDTO dto,
            java.security.Principal principal) {
        if (principal == null) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "로그인이 필요합니다.");
            return ResponseEntity.status(401).body(errorResponse);
        }

        try {
            memberService.updateMemberInfo(principal.getName(), dto);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "회원 정보가 성공적으로 수정되었습니다.");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "수정 실패: " + e.getMessage());
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }
}

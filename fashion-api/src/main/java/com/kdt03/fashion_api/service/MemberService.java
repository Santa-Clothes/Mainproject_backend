package com.kdt03.fashion_api.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.kdt03.fashion_api.domain.Member;
import com.kdt03.fashion_api.domain.dto.MemberLoginDTO;
import com.kdt03.fashion_api.domain.dto.MemberResponseDTO;
import com.kdt03.fashion_api.domain.dto.MemberSignupDTO;
import com.kdt03.fashion_api.domain.dto.MemberUpdateDTO;
import com.kdt03.fashion_api.repository.MemberRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class MemberService {
    private final MemberRepository memberRepo;
    private final PasswordEncoder passwordEncoder;
    private final ImageUploadService imageUploadService;

    // 회원가입 (프로필 이미지 선택사항)
    @Transactional
    public void signup(MemberSignupDTO dto, MultipartFile profileImage) {
        String raw = dto.getPassword();
        String encoded = passwordEncoder.encode(raw);

        Member member = Member.builder()
                .id(dto.getId())
                .nickname(dto.getNickname())
                .password(encoded)
                .provider("local")
                .build();

        memberRepo.save(member);

        // 프로필 이미지가 있으면 업로드
        if (profileImage != null && !profileImage.isEmpty()) {
            try {
                log.info("프로필 이미지 업로드 시작: {}", profileImage.getOriginalFilename());
                String profileUrl = imageUploadService.uploadProfileImage(profileImage, dto.getId());
                log.info("프로필 이미지 업로드 성공, URL: {}", profileUrl);
                member.setProfile(profileUrl);
                memberRepo.save(member);
            } catch (Exception e) {
                // 프로필 이미지 업로드 실패해도 회원가입은 성공으로 처리
                log.warn("프로필 이미지 업로드 실패 (회원가입은 성공): {}", e.getMessage(), e);
            }
        } else {
            log.debug("프로필 이미지 없음 - 기본 이미지 설정");
            member.setProfile(
                    "https://fjoylosbfvojioljibku.supabase.co/storage/v1/object/public/profileimage/default.svg");
            memberRepo.save(member);
        }
    }

    // 로그인
    public Member login(MemberLoginDTO dto) {
        Member member = memberRepo.findById(dto.getId())
                .orElseThrow(() -> new RuntimeException("가입되지 않은 회원입니다."));

        if (!passwordEncoder.matches(dto.getPassword(), member.getPassword())) {
            throw new RuntimeException("비밀번호가 다릅니다.");
        }

        return member;
    }

    // 회원 탈퇴
    public void withdraw(String memberId, String password) {
        Member member = memberRepo.findById(memberId)
                .orElseThrow(() -> new RuntimeException("가입되지 않은 회원입니다."));

        if (!passwordEncoder.matches(password, member.getPassword())) {
            throw new RuntimeException("비밀번호가 다릅니다.");
        }

        memberRepo.delete(member);

    }

    // 전체 회원 조회 (테스트용)
    public java.util.List<MemberResponseDTO> getAllMembers() {
        return memberRepo.findAll().stream().map(member -> {
            return MemberResponseDTO.builder()
                    .id(member.getId())
                    .nickname(member.getNickname())
                    .provider(member.getProvider())
                    .profile(member.getProfile())
                    .build();
        }).collect(java.util.stream.Collectors.toList());
    }

    // 회원 정보 수정
    @Transactional
    public void updateMemberInfo(String id, MemberUpdateDTO dto) {
        Member member = memberRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        if (dto.getNickname() != null && !dto.getNickname().isEmpty()) {
            member.setNickname(dto.getNickname());
        }

        if (dto.getPassword() != null && !dto.getPassword().isEmpty()) {
            member.setPassword(passwordEncoder.encode(dto.getPassword()));
        }

        memberRepo.save(member);
    }

}
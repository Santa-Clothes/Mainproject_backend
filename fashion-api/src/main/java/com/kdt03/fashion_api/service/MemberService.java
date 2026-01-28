package com.kdt03.fashion_api.service;

import java.util.List;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.kdt03.fashion_api.domain.Member;
import com.kdt03.fashion_api.domain.dto.MemberDTO;
import com.kdt03.fashion_api.domain.dto.MemberLoginDTO;
import com.kdt03.fashion_api.domain.dto.MemberSignupDTO;
import com.kdt03.fashion_api.repository.MemberRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class MemberService {
    private final MemberRepository memberRepo;
    private final PasswordEncoder passwordEncoder;

    public List<MemberDTO> getAllMembers() {
        return memberRepo.findAll().stream().map(member -> MemberDTO.builder()
                .id(member.getId())
                .nickname(member.getNickname())
                .provider(member.getProvider())
                .build()).toList();
    }

    // 회원가입
    public void signup(MemberSignupDTO dto) {
        String raw = dto.getPassword();
        String encoded = passwordEncoder.encode(raw);

        Member member = Member.builder().id(dto.getId()).nickname(dto.getNickname()).password(encoded)
                .provider(dto.getProvider()).build();

        memberRepo.save(member);
    }

    public String login(MemberLoginDTO dto) {
        Member member = memberRepo.findById(dto.getId())
                .orElseThrow(() -> new RuntimeException("가입되지 않은 회원입니다."));


        if (!passwordEncoder.matches(dto.getPassword(), member.getPassword())) {
            throw new RuntimeException("비밀번호가 다릅니다.");
        }

        return "로그인 성공";
    }
}
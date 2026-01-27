package com.kdt03.fashion_api.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.kdt03.fashion_api.domain.dto.MemberDTO;
import com.kdt03.fashion_api.repository.MemberRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class MemberService {
    private final MemberRepository memberRepo;
    
    public List<MemberDTO> getAllMembers() {
        return memberRepo.findAll().stream().map(member -> MemberDTO.builder()
        .id(member.getId())
        .nickname(member.getNickname())
        .provider(member.getProvider())
        .build()).toList();
    }
}
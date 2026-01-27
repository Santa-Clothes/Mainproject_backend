package com.kdt03.fashion_api.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.kdt03.fashion_api.service.MemberService;
import com.kdt03.fashion_api.domain.dto.MemberDTO;
import org.springframework.web.bind.annotation.GetMapping;
import java.util.List;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/members")
public class MemberController {
    private final MemberService memberService;
    
    @GetMapping("/list")
    public List<MemberDTO> getMembers() {
        return memberService.getAllMembers();
    }
}
package com.kdt03.fashion_api.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kdt03.fashion_api.domain.Member;

public interface MemberRepository extends JpaRepository<Member, String> {
    
}
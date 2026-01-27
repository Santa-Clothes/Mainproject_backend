package com.kdt03.fashion_api.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class Member {
    @Id
    private String id;
    private String password;
    private String nickname;
    private String provider;
}
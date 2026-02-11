package com.kdt03.fashion_api.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "회원 정보 수정 요청 DTO")
public class MemberUpdateDTO {

    @Schema(description = "변경할 닉네임 (기존 유지 시 null 또는 빈 문자열)", example = "새닉네임")
    private String nickname;

    @Schema(description = "변경할 비밀번호 (기존 유지 시 null 또는 빈 문자열)", example = "newpassword123")
    private String password;
}

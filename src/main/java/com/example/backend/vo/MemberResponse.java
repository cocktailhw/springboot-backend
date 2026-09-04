package com.example.backend.vo;

import java.time.LocalDateTime;

import com.example.backend.domain.Member;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MemberResponse {

    private final Long id;
    private final String username;
    private final String role;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    public static MemberResponse from(Member member) {
        return MemberResponse.builder()
                .id(member.getId())
                .username(member.getUsername())
                .role(member.getRole())
                .createdAt(member.getCreatedAt())
                .updatedAt(member.getUpdatedAt())
                .build();
    }
}

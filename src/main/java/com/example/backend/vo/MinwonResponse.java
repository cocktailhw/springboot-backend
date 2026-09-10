package com.example.backend.vo;

import java.time.LocalDateTime;

import com.example.backend.domain.Minwon;
import com.example.backend.domain.MinwonStatus;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MinwonResponse {

    private final Long id;
    private final Long memberId;
    private final String memberUsername;
    private final String title;
    private final String content;
    private final MinwonStatus status;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    public static MinwonResponse from(Minwon minwon) {
        return MinwonResponse.builder()
                .id(minwon.getId())
                .memberId(minwon.getMember().getId())
                .memberUsername(minwon.getMember().getUsername())
                .title(minwon.getTitle())
                .content(minwon.getContent())
                .status(minwon.getStatus())
                .createdAt(minwon.getCreatedAt())
                .updatedAt(minwon.getUpdatedAt())
                .build();
    }
}

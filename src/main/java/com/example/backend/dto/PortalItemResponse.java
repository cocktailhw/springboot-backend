package com.example.backend.dto;

import java.time.LocalDateTime;

import com.example.backend.domain.ItemType;
import com.example.backend.domain.PortalItem;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PortalItemResponse {

    private final Long id;
    private final ItemType type;
    private final String title;
    private final String content;
    private final String department;
    private final String status;
    private final int viewCount;
    private final LocalDateTime createdAt;

    public static PortalItemResponse from(PortalItem item) {
        return PortalItemResponse.builder()
                .id(item.getId())
                .type(item.getType())
                .title(item.getTitle())
                .content(item.getContent())
                .department(item.getDepartment())
                .status(item.getStatus())
                .viewCount(item.getViewCount())
                .createdAt(item.getCreatedAt())
                .build();
    }
}

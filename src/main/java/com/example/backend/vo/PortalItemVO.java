package com.example.backend.vo;

import java.time.LocalDateTime;

import com.example.backend.domain.ItemType;
import com.example.backend.domain.PortalItem;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PortalItemVO {

    private final Long id;
    private final ItemType type;
    private final String title;
    private final String content;
    private final String department;
    private final String status;
    private final int viewCount;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    public static PortalItemVO from(PortalItem entity) {
        return PortalItemVO.builder()
                .id(entity.getId())
                .type(entity.getType())
                .title(entity.getTitle())
                .content(entity.getContent())
                .department(entity.getDepartment())
                .status(entity.getStatus())
                .viewCount(entity.getViewCount())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}

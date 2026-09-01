package com.example.backend.vo;

import java.time.LocalDateTime;

import com.example.backend.domain.ItemType;
import com.example.backend.domain.PortalItem;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PortalItemVO {

    private static final String DOWNLOAD_PATH_PREFIX = "/api/v1/portal/files/download/";

    private final Long id;
    private final ItemType type;
    private final String title;
    private final String content;
    private final String department;
    private final String status;
    private final int viewCount;
    private final String originalFileName;
    private final String downloadUrl;
    private final Long fileSize;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    public static PortalItemVO from(PortalItem entity) {
        String storedFileName = entity.getStoredFileName();
        String downloadUrl = storedFileName != null && !storedFileName.isBlank()
                ? DOWNLOAD_PATH_PREFIX + storedFileName
                : null;

        return PortalItemVO.builder()
                .id(entity.getId())
                .type(entity.getType())
                .title(entity.getTitle())
                .content(entity.getContent())
                .department(entity.getDepartment())
                .status(entity.getStatus())
                .viewCount(entity.getViewCount())
                .originalFileName(entity.getOriginalFileName())
                .downloadUrl(downloadUrl)
                .fileSize(entity.getFileSize())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}

package com.example.backend.vo;

import com.example.backend.domain.ItemType;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PortalItemRequest {

    @NotNull(message = "유형은 필수입니다.")
    private ItemType type;

    @NotBlank(message = "제목은 필수입니다.")
    private String title;

    @NotBlank(message = "내용은 필수입니다.")
    private String content;

    @NotBlank(message = "담당 부서는 필수입니다.")
    private String department;

    @NotBlank(message = "상태는 필수입니다.")
    private String status;
}

package com.example.backend.vo;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class MinwonRequest {

    @NotBlank(message = "민원 제목은 필수입니다.")
    @Size(max = 200, message = "민원 제목은 200자 이하여야 합니다.")
    private String title;

    @Size(max = 5000, message = "민원 내용은 5000자 이하여야 합니다.")
    private String content;
}

package com.example.backend.vo;

import com.example.backend.domain.MinwonStatus;

import jakarta.validation.constraints.NotNull;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class MinwonStatusUpdateRequest {

    @NotNull(message = "민원 상태는 필수입니다.")
    private MinwonStatus status;
}

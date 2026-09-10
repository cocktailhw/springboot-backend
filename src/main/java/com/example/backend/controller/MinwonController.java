package com.example.backend.controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.backend.common.ResultResponse;
import com.example.backend.service.MinwonService;
import com.example.backend.vo.MinwonRequest;
import com.example.backend.vo.MinwonResponse;
import com.example.backend.vo.MinwonStatusUpdateRequest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Tag(name = "Minwon", description = "민원 접수 · 내 민원 · 관리자 처리")
@RestController
@RequestMapping("/api/v1/minwon")
@RequiredArgsConstructor
public class MinwonController {

    private final MinwonService minwonService;

    @Operation(summary = "민원 신청", description = "로그인한 시민(ROLE_USER)이 민원을 접수합니다. 초기 상태는 WAITING 입니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "접수 성공",
                    content = @Content(schema = @Schema(implementation = ResultResponse.class))),
            @ApiResponse(responseCode = "400", description = "요청 값 검증 실패"),
            @ApiResponse(responseCode = "401", description = "인증 필요"),
            @ApiResponse(responseCode = "403", description = "권한 없음")
    })
    @PreAuthorize("hasRole('USER')")
    @PostMapping
    public ResultResponse<MinwonResponse> applyMinwon(@Valid @RequestBody MinwonRequest request) {
        return ResultResponse.ok("민원이 접수되었습니다.", minwonService.applyMinwon(request));
    }

    @Operation(summary = "내 민원 내역 조회", description = "현재 로그인한 사용자가 접수한 민원 목록을 최신순으로 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공",
                    content = @Content(schema = @Schema(implementation = ResultResponse.class))),
            @ApiResponse(responseCode = "401", description = "인증 필요")
    })
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/my")
    public ResultResponse<List<MinwonResponse>> getMyMinwons() {
        return ResultResponse.ok(minwonService.getMyMinwons());
    }

    @Operation(summary = "전체 민원 조회(관리자)", description = "관리자가 전체 민원 목록을 페이징으로 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공",
                    content = @Content(schema = @Schema(implementation = ResultResponse.class))),
            @ApiResponse(responseCode = "401", description = "인증 필요"),
            @ApiResponse(responseCode = "403", description = "관리자 권한 필요")
    })
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResultResponse<Page<MinwonResponse>> getAllMinwons(
            @PageableDefault(size = 10) Pageable pageable) {
        return ResultResponse.ok(minwonService.getAllMinwons(pageable));
    }

    @Operation(summary = "민원 상태 변경(관리자)",
            description = "관리자가 민원 상태를 WAITING / IN_PROGRESS / COMPLETED 로 변경합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "상태 변경 성공",
                    content = @Content(schema = @Schema(implementation = ResultResponse.class))),
            @ApiResponse(responseCode = "404", description = "민원 없음"),
            @ApiResponse(responseCode = "403", description = "관리자 권한 필요")
    })
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}/status")
    public ResultResponse<MinwonResponse> updateMinwonStatus(
            @PathVariable Long id,
            @Valid @RequestBody MinwonStatusUpdateRequest request) {
        return ResultResponse.ok("민원 상태가 변경되었습니다.",
                minwonService.updateMinwonStatus(id, request.getStatus()));
    }
}

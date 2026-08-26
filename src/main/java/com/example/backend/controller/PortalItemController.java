package com.example.backend.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.backend.common.ApiResponse;
import com.example.backend.domain.ItemType;
import com.example.backend.dto.PortalItemResponse;
import com.example.backend.service.PortalItemService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/portal")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class PortalItemController {

    private final PortalItemService portalItemService;

    @GetMapping
    public ApiResponse<Page<PortalItemResponse>> getItems(
            @RequestParam ItemType type,
            @PageableDefault(size = 10) Pageable pageable) {
        return ApiResponse.success(portalItemService.findByType(type, pageable));
    }
}

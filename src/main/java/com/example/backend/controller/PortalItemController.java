package com.example.backend.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.backend.common.ResultResponse;
import com.example.backend.domain.ItemType;
import com.example.backend.service.PortalItemService;
import com.example.backend.vo.PortalItemVO;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/portal")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class PortalItemController {

    private final PortalItemService portalItemService;

    @GetMapping
    public ResultResponse<Page<PortalItemVO>> getPortalItems(
            @RequestParam ItemType type,
            @PageableDefault(size = 10) Pageable pageable) {
        return ResultResponse.ok(portalItemService.selectPortalItemList(type, pageable));
    }
}

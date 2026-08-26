package com.example.backend.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.backend.common.ResultResponse;
import com.example.backend.domain.ItemType;
import com.example.backend.service.PortalItemService;
import com.example.backend.vo.PortalItemRequest;
import com.example.backend.vo.PortalItemVO;

import jakarta.validation.Valid;
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

    @PostMapping
    public ResultResponse<PortalItemVO> createPortalItem(@Valid @RequestBody PortalItemRequest request) {
        return ResultResponse.ok(portalItemService.createItem(request));
    }

    @PutMapping("/{id}")
    public ResultResponse<PortalItemVO> updatePortalItem(
            @PathVariable Long id,
            @Valid @RequestBody PortalItemRequest request) {
        return ResultResponse.ok(portalItemService.updateItem(id, request));
    }

    @DeleteMapping("/{id}")
    public ResultResponse<Void> deletePortalItem(@PathVariable Long id) {
        portalItemService.deleteItem(id);
        return ResultResponse.ok("정상적으로 삭제되었습니다.", null);
    }
}

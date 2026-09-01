package com.example.backend.controller;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.MediaTypeFactory;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.backend.common.ResultResponse;
import com.example.backend.domain.ItemType;
import com.example.backend.service.FileDownloadInfo;
import com.example.backend.service.PortalItemService;
import com.example.backend.vo.PortalItemRequest;
import com.example.backend.vo.PortalItemVO;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/portal")
@RequiredArgsConstructor
public class PortalItemController {

    private final PortalItemService portalItemService;

    @GetMapping
    public ResultResponse<Page<PortalItemVO>> getPortalItems(
            @RequestParam ItemType type,
            @PageableDefault(size = 10) Pageable pageable) {
        return ResultResponse.ok(portalItemService.selectPortalItemList(type, pageable));
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResultResponse<PortalItemVO> createPortalItem(
            @Valid @RequestPart("data") PortalItemRequest request,
            @RequestPart(value = "file", required = false) MultipartFile file) {
        return ResultResponse.ok(portalItemService.createItem(request, file));
    }

    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResultResponse<PortalItemVO> updatePortalItem(
            @PathVariable Long id,
            @Valid @RequestPart("data") PortalItemRequest request,
            @RequestPart(value = "file", required = false) MultipartFile file) {
        return ResultResponse.ok(portalItemService.updateItem(id, request, file));
    }

    @DeleteMapping("/{id}")
    public ResultResponse<Void> deletePortalItem(@PathVariable Long id) {
        portalItemService.deleteItem(id);
        return ResultResponse.ok("정상적으로 삭제되었습니다.", null);
    }

    @GetMapping("/files/download/{storedFileName}")
    public ResponseEntity<Resource> downloadFile(@PathVariable String storedFileName) {
        FileDownloadInfo fileInfo = portalItemService.getFileDownloadInfo(storedFileName);
        String encodedFileName = URLEncoder.encode(fileInfo.originalFileName(), StandardCharsets.UTF_8)
                .replace("+", "%20");

        MediaType mediaType = MediaTypeFactory.getMediaType(fileInfo.originalFileName())
                .orElse(MediaType.APPLICATION_OCTET_STREAM);

        return ResponseEntity.ok()
                .contentType(mediaType)
                .contentLength(fileInfo.contentLength())
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + fileInfo.originalFileName()
                                + "\"; filename*=UTF-8''" + encodedFileName)
                .body(fileInfo.resource());
    }
}

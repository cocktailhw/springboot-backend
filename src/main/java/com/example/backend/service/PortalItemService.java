package com.example.backend.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import com.example.backend.domain.ItemType;
import com.example.backend.domain.PortalCategory;
import com.example.backend.vo.PortalItemRequest;
import com.example.backend.vo.PortalItemVO;

public interface PortalItemService {

    Page<PortalItemVO> selectPortalItemList(
            ItemType type, PortalCategory category, String keyword, Pageable pageable);

    PortalItemVO createItem(PortalItemRequest request, MultipartFile file);

    PortalItemVO updateItem(Long id, PortalItemRequest request, MultipartFile file);

    void deleteItem(Long id);

    FileDownloadInfo getFileDownloadInfo(String storedFileName);
}

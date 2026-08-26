package com.example.backend.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.backend.domain.ItemType;
import com.example.backend.dto.PortalItemResponse;
import com.example.backend.repository.PortalItemRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PortalItemService {

    private final PortalItemRepository portalItemRepository;

    public Page<PortalItemResponse> findByType(ItemType type, Pageable pageable) {
        return portalItemRepository.findByTypeOrderByCreatedAtDesc(type, pageable)
                .map(PortalItemResponse::from);
    }
}

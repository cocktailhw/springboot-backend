package com.example.backend.service;

import java.util.List;

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

    public List<PortalItemResponse> findByType(ItemType type) {
        return portalItemRepository.findByTypeOrderByCreatedAtDesc(type).stream()
                .map(PortalItemResponse::from)
                .toList();
    }
}

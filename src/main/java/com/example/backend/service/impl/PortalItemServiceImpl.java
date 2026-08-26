package com.example.backend.service.impl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.backend.domain.ItemType;
import com.example.backend.repository.PortalItemRepository;
import com.example.backend.service.PortalItemService;
import com.example.backend.vo.PortalItemVO;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PortalItemServiceImpl implements PortalItemService {

    private final PortalItemRepository portalItemRepository;

    @Override
    public Page<PortalItemVO> selectPortalItemList(ItemType type, Pageable pageable) {
        return portalItemRepository.findByTypeOrderByCreatedAtDesc(type, pageable)
                .map(PortalItemVO::from);
    }
}

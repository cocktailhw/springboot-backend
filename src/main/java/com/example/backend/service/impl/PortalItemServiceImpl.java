package com.example.backend.service.impl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.backend.domain.ItemType;
import com.example.backend.domain.PortalItem;
import com.example.backend.repository.PortalItemRepository;
import com.example.backend.service.PortalItemService;
import com.example.backend.vo.PortalItemRequest;
import com.example.backend.vo.PortalItemVO;

import jakarta.persistence.EntityNotFoundException;
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

    @Override
    @Transactional
    public PortalItemVO createItem(PortalItemRequest request) {
        PortalItem item = PortalItem.builder()
                .type(request.getType())
                .title(request.getTitle())
                .content(request.getContent())
                .department(request.getDepartment())
                .status(request.getStatus())
                .viewCount(0)
                .build();

        return PortalItemVO.from(portalItemRepository.save(item));
    }

    @Override
    @Transactional
    public PortalItemVO updateItem(Long id, PortalItemRequest request) {
        PortalItem item = findPortalItem(id);
        item.update(
                request.getType(),
                request.getTitle(),
                request.getContent(),
                request.getDepartment(),
                request.getStatus()
        );

        return PortalItemVO.from(item);
    }

    @Override
    @Transactional
    public void deleteItem(Long id) {
        PortalItem item = findPortalItem(id);
        portalItemRepository.delete(item);
    }

    private PortalItem findPortalItem(Long id) {
        return portalItemRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("포털 항목을 찾을 수 없습니다. id=" + id));
    }
}

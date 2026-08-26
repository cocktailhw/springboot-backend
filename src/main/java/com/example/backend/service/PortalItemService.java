package com.example.backend.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.example.backend.domain.ItemType;
import com.example.backend.vo.PortalItemRequest;
import com.example.backend.vo.PortalItemVO;

public interface PortalItemService {

    Page<PortalItemVO> selectPortalItemList(ItemType type, Pageable pageable);

    PortalItemVO createItem(PortalItemRequest request);

    PortalItemVO updateItem(Long id, PortalItemRequest request);

    void deleteItem(Long id);
}

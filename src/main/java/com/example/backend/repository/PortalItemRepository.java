package com.example.backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.backend.domain.ItemType;
import com.example.backend.domain.PortalItem;

public interface PortalItemRepository extends JpaRepository<PortalItem, Long> {

    List<PortalItem> findByTypeOrderByCreatedAtDesc(ItemType type);
}

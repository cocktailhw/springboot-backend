package com.example.backend.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.backend.domain.ItemType;
import com.example.backend.domain.PortalItem;

public interface PortalItemRepository extends JpaRepository<PortalItem, Long> {

    Page<PortalItem> findByTypeOrderByCreatedAtDesc(ItemType type, Pageable pageable);

    Optional<PortalItem> findByStoredFileName(String storedFileName);
}

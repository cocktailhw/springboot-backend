package com.example.backend.repository;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.backend.domain.ItemType;
import com.example.backend.domain.PortalCategory;
import com.example.backend.domain.PortalItem;

public interface PortalItemRepository extends JpaRepository<PortalItem, Long> {

    Page<PortalItem> findByTypeOrderByCreatedAtDesc(ItemType type, Pageable pageable);

    Page<PortalItem> findByTypeAndCategoryOrderByCreatedAtDesc(
            ItemType type, PortalCategory category, Pageable pageable);

    /**
     * type 필수, category·keyword는 선택.
     * keyword가 있으면 제목·부서·내용에 대해 LIKE 검색한다.
     * content는 @Lob(CLOB/TEXT) 이므로 Hibernate 6에서 LOWER 적용 전 CAST 필요.
     */
    @Query("""
            SELECT p FROM PortalItem p
            WHERE p.type = :type
              AND (:category IS NULL OR p.category = :category)
              AND (
                   :keyword IS NULL
                   OR :keyword = ''
                   OR LOWER(p.title) LIKE LOWER(CONCAT('%', :keyword, '%'))
                   OR LOWER(p.department) LIKE LOWER(CONCAT('%', :keyword, '%'))
                   OR LOWER(CAST(p.content AS string)) LIKE LOWER(CONCAT('%', :keyword, '%'))
              )
            """)
    Page<PortalItem> search(
            @Param("type") ItemType type,
            @Param("category") PortalCategory category,
            @Param("keyword") String keyword,
            Pageable pageable);

    Optional<PortalItem> findByStoredFileName(String storedFileName);

    boolean existsByTypeAndTitleAndDepartmentAndCreatedAtAfter(
            ItemType type, String title, String department, LocalDateTime createdAt);
}

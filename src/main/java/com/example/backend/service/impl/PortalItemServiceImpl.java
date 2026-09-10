package com.example.backend.service.impl;

import java.io.InputStream;
import java.time.LocalDateTime;

import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.example.backend.common.DuplicateRequestException;
import com.example.backend.domain.ItemType;
import com.example.backend.domain.PortalCategory;
import com.example.backend.domain.PortalItem;
import com.example.backend.repository.PortalItemRepository;
import com.example.backend.service.FileDownloadInfo;
import com.example.backend.service.FileStorageService;
import com.example.backend.service.PortalItemService;
import com.example.backend.vo.PortalItemRequest;
import com.example.backend.vo.PortalItemVO;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PortalItemServiceImpl implements PortalItemService {

    private static final long DUPLICATE_WINDOW_SECONDS = 10;

    private final PortalItemRepository portalItemRepository;
    private final FileStorageService fileStorageService;

    @Override
    public Page<PortalItemVO> selectPortalItemList(
            ItemType type, PortalCategory category, String keyword, Pageable pageable) {
        String normalizedKeyword = StringUtils.hasText(keyword) ? keyword.trim() : null;
        Pageable sortedPageable = pageable.getSort().isSorted()
                ? pageable
                : PageRequest.of(
                        pageable.getPageNumber(),
                        pageable.getPageSize(),
                        Sort.by(Sort.Direction.DESC, "createdAt"));
        return portalItemRepository
                .search(type, category, normalizedKeyword, sortedPageable)
                .map(PortalItemVO::from);
    }

    @Override
    @Transactional
    public PortalItemVO createItem(PortalItemRequest request, MultipartFile file) {
        validateNotDuplicated(request);

        String originalFileName = null;
        String storedFileName = null;
        Long fileSize = null;

        if (hasFile(file)) {
            FileStorageService.StoredFile storedFile = fileStorageService.store(file);
            originalFileName = storedFile.originalFileName();
            storedFileName = storedFile.storedFileName();
            fileSize = storedFile.fileSize();
        }

        PortalCategory category = request.getCategory() != null
                ? request.getCategory()
                : PortalCategory.NOTICE;

        PortalItem item = PortalItem.builder()
                .type(request.getType())
                .category(category)
                .title(request.getTitle())
                .content(request.getContent())
                .department(request.getDepartment())
                .status(request.getStatus())
                .viewCount(0)
                .originalFileName(originalFileName)
                .storedFileName(storedFileName)
                .fileSize(fileSize)
                .build();

        return PortalItemVO.from(portalItemRepository.save(item));
    }

    @Override
    @Transactional
    public PortalItemVO updateItem(Long id, PortalItemRequest request, MultipartFile file) {
        PortalItem item = findPortalItem(id);
        PortalCategory category = request.getCategory() != null
                ? request.getCategory()
                : item.getCategory();

        item.update(
                request.getType(),
                category,
                request.getTitle(),
                request.getContent(),
                request.getDepartment(),
                request.getStatus()
        );

        if (hasFile(file)) {
            fileStorageService.deleteQuietly(item.getStoredFileName());
            FileStorageService.StoredFile storedFile = fileStorageService.store(file);
            item.updateFileMetadata(
                    storedFile.originalFileName(),
                    storedFile.storedFileName(),
                    storedFile.fileSize()
            );
        }

        return PortalItemVO.from(item);
    }

    @Override
    @Transactional
    public void deleteItem(Long id) {
        PortalItem item = findPortalItem(id);
        fileStorageService.deleteQuietly(item.getStoredFileName());
        portalItemRepository.delete(item);
    }

    @Override
    public FileDownloadInfo getFileDownloadInfo(String storedFileName) {
        PortalItem item = portalItemRepository.findByStoredFileName(storedFileName)
                .orElseThrow(() -> new EntityNotFoundException("파일을 찾을 수 없습니다."));

        InputStream inputStream = fileStorageService.download(storedFileName);
        long contentLength = item.getFileSize() != null && item.getFileSize() > 0
                ? item.getFileSize()
                : fileStorageService.getContentLength(storedFileName);
        String originalFileName = StringUtils.hasText(item.getOriginalFileName())
                ? item.getOriginalFileName()
                : storedFileName;

        return new FileDownloadInfo(
                new InputStreamResource(inputStream),
                originalFileName,
                contentLength
        );
    }

    /**
     * 새로고침·더블클릭으로 동일 게시글이 연속 접수되는 것을 차단한다.
     */
    private void validateNotDuplicated(PortalItemRequest request) {
        LocalDateTime threshold = LocalDateTime.now().minusSeconds(DUPLICATE_WINDOW_SECONDS);
        boolean duplicated = portalItemRepository.existsByTypeAndTitleAndDepartmentAndCreatedAtAfter(
                request.getType(), request.getTitle(), request.getDepartment(), threshold);

        if (duplicated) {
            throw new DuplicateRequestException("동일한 제목의 게시글이 이미 접수되었습니다.");
        }
    }

    private PortalItem findPortalItem(Long id) {
        return portalItemRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("포털 항목을 찾을 수 없습니다."));
    }

    private boolean hasFile(MultipartFile file) {
        return file != null && !file.isEmpty();
    }
}

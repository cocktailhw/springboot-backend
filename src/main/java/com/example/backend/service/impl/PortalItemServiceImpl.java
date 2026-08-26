package com.example.backend.service.impl;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.example.backend.domain.ItemType;
import com.example.backend.domain.PortalItem;
import com.example.backend.repository.PortalItemRepository;
import com.example.backend.service.PortalItemService;
import com.example.backend.vo.PortalItemRequest;
import com.example.backend.vo.PortalItemVO;

import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PortalItemServiceImpl implements PortalItemService {

    private final PortalItemRepository portalItemRepository;

    @Value("${file.upload-dir:./uploads}")
    private String uploadDir;

    private Path uploadPath;

    @PostConstruct
    void initUploadDirectory() {
        this.uploadPath = Paths.get(uploadDir).toAbsolutePath().normalize();
        try {
            Files.createDirectories(this.uploadPath);
        } catch (IOException e) {
            throw new IllegalStateException("업로드 디렉터리를 생성할 수 없습니다: " + uploadPath, e);
        }
    }

    @Override
    public Page<PortalItemVO> selectPortalItemList(ItemType type, Pageable pageable) {
        return portalItemRepository.findByTypeOrderByCreatedAtDesc(type, pageable)
                .map(PortalItemVO::from);
    }

    @Override
    @Transactional
    public PortalItemVO createItem(PortalItemRequest request, MultipartFile file) {
        String originalFileName = null;
        String storedFileName = null;
        Long fileSize = null;

        if (hasFile(file)) {
            StoredFile storedFile = storeFile(file);
            originalFileName = storedFile.originalFileName();
            storedFileName = storedFile.storedFileName();
            fileSize = storedFile.fileSize();
        }

        PortalItem item = PortalItem.builder()
                .type(request.getType())
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
        item.update(
                request.getType(),
                request.getTitle(),
                request.getContent(),
                request.getDepartment(),
                request.getStatus()
        );

        if (hasFile(file)) {
            deletePhysicalFileQuietly(item.getStoredFileName());
            StoredFile storedFile = storeFile(file);
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
        deletePhysicalFileQuietly(item.getStoredFileName());
        portalItemRepository.delete(item);
    }

    @Override
    public Resource loadFileAsResource(String storedFileName) {
        validateStoredFileName(storedFileName);
        try {
            Path filePath = resolveSafeFilePath(storedFileName);
            Resource resource = new UrlResource(filePath.toUri());
            if (!resource.exists() || !resource.isReadable()) {
                throw new EntityNotFoundException("파일을 찾을 수 없습니다. storedFileName=" + storedFileName);
            }
            return resource;
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException("잘못된 파일 경로입니다.", e);
        }
    }

    @Override
    public String getOriginalFileName(String storedFileName) {
        return portalItemRepository.findByStoredFileName(storedFileName)
                .map(PortalItem::getOriginalFileName)
                .filter(StringUtils::hasText)
                .orElse(storedFileName);
    }

    private PortalItem findPortalItem(Long id) {
        return portalItemRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("포털 항목을 찾을 수 없습니다. id=" + id));
    }

    private boolean hasFile(MultipartFile file) {
        return file != null && !file.isEmpty();
    }

    private StoredFile storeFile(MultipartFile file) {
        String originalFileName = StringUtils.cleanPath(
                file.getOriginalFilename() == null ? "unknown" : file.getOriginalFilename()
        );
        if (originalFileName.contains("..")) {
            throw new IllegalArgumentException("파일명에 상위 경로 문자(..)를 포함할 수 없습니다.");
        }

        String extension = "";
        int dotIndex = originalFileName.lastIndexOf('.');
        if (dotIndex >= 0) {
            extension = originalFileName.substring(dotIndex);
        }

        String storedFileName = UUID.randomUUID() + extension;
        Path target = resolveSafeFilePath(storedFileName);

        try {
            Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new IllegalStateException("파일 저장에 실패했습니다.", e);
        }

        return new StoredFile(originalFileName, storedFileName, file.getSize());
    }

    private void deletePhysicalFileQuietly(String storedFileName) {
        if (!StringUtils.hasText(storedFileName)) {
            return;
        }
        try {
            Files.deleteIfExists(resolveSafeFilePath(storedFileName));
        } catch (IOException | IllegalArgumentException ignored) {
            // best-effort cleanup
        }
    }

    private Path resolveSafeFilePath(String storedFileName) {
        Path resolved = uploadPath.resolve(storedFileName).normalize();
        if (!resolved.startsWith(uploadPath)) {
            throw new IllegalArgumentException("허용되지 않은 파일 경로입니다.");
        }
        return resolved;
    }

    private void validateStoredFileName(String storedFileName) {
        if (!StringUtils.hasText(storedFileName) || storedFileName.contains("..") || storedFileName.contains("/")
                || storedFileName.contains("\\")) {
            throw new IllegalArgumentException("저장된 파일명이 올바르지 않습니다.");
        }
    }

    private record StoredFile(String originalFileName, String storedFileName, long fileSize) {
    }
}

package com.aitasker.file.service;

import com.aitasker.common.enums.Role;
import com.aitasker.exception.BadRequestException;
import com.aitasker.exception.ForbiddenException;
import com.aitasker.exception.ResourceNotFoundException;
import com.aitasker.file.entity.Attachment;
import com.aitasker.file.repository.AttachmentRepository;
import com.aitasker.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FileStorageService {

    private final AttachmentRepository attachmentRepository;

    @Value("${app.upload.dir:uploads}")
    private String uploadDir;

    public Attachment store(MultipartFile file, User uploader) {
        if (file == null || file.isEmpty()) {
            throw new BadRequestException("File không được để trống");
        }
        try {
            Path dir = Path.of(uploadDir);
            Files.createDirectories(dir);

            String extension = "";
            String original = file.getOriginalFilename();
            if (original != null && original.contains(".")) {
                extension = original.substring(original.lastIndexOf('.'));
            }
            String storedName = UUID.randomUUID() + extension;

            Path target = dir.resolve(storedName);
            Files.copy(file.getInputStream(), target);

            Attachment attachment = Attachment.builder()
                    .originalFileName(original)
                    .storedFileName(storedName)
                    .contentType(file.getContentType())
                    .fileSize(file.getSize())
                    .uploadedBy(uploader)
                    .build();

            return attachmentRepository.save(attachment);
        } catch (IOException e) {
            throw new BadRequestException("Lưu file thất bại: " + e.getMessage());
        }
    }

    public Attachment getMetadata(Long id) {
        return attachmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy file"));
    }

    public Attachment getMetadataForDownload(Long id, User requester) {
        Attachment attachment = getMetadata(id);
        boolean isOwner = attachment.getUploadedBy().getId().equals(requester.getId());
        boolean isAdmin = requester.getRole() == Role.ADMIN;
        if (!isOwner && !isAdmin) {
            throw new ForbiddenException("Bạn không có quyền tải file này.");
        }
        return attachment;
    }

    public Resource loadAsResource(Long id) {
        Attachment attachment = getMetadata(id);
        Path path = Path.of(uploadDir).resolve(attachment.getStoredFileName());
        if (!Files.exists(path)) {
            throw new ResourceNotFoundException("File vật lý không tồn tại");
        }
        return new FileSystemResource(path);
    }

    public void delete(Long id, User requester) {
        Attachment attachment = getMetadata(id);
        boolean isOwner = attachment.getUploadedBy().getId().equals(requester.getId());
        boolean isAdmin = requester.getRole() == com.aitasker.common.enums.Role.ADMIN;
        if (!isOwner && !isAdmin) {
            throw new com.aitasker.exception.ForbiddenException("Bạn không có quyền xóa file này");
        }
        Path path = Path.of(uploadDir).resolve(attachment.getStoredFileName());
        try {
            Files.deleteIfExists(path);
        } catch (IOException ignored) {
        }
        attachmentRepository.delete(attachment);
    }
}

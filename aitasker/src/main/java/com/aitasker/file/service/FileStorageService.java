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
import java.util.Arrays;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FileStorageService {

    private final AttachmentRepository attachmentRepository;

    @Value("${app.upload.dir:uploads}")
    private String uploadDir;

    @Value("${app.upload.max-size-bytes:10485760}")
    private long maxSizeBytes;

    @Value("${app.upload.allowed-content-types:application/pdf,image/jpeg,image/png,text/plain}")
    private String allowedContentTypes;

    public Attachment store(MultipartFile file, User uploader) {
        if (file == null || file.isEmpty()) {
            throw new BadRequestException("File khong duoc de trong");
        }
        validateFile(file);

        try {
            Path dir = Path.of(uploadDir).toAbsolutePath().normalize();
            Files.createDirectories(dir);

            String original = file.getOriginalFilename();
            String extension = "";
            if (original != null && original.contains(".")) {
                extension = original.substring(original.lastIndexOf('.')).toLowerCase(Locale.ROOT);
            }

            String storedName = UUID.randomUUID() + extension;
            Path target = dir.resolve(storedName).normalize();
            if (!target.startsWith(dir)) {
                throw new BadRequestException("Ten file khong hop le");
            }

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
            throw new BadRequestException("Luu file that bai: " + e.getMessage());
        }
    }

    public Attachment getMetadata(Long id) {
        return attachmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Khong tim thay file"));
    }

    public Attachment getMetadataForDownload(Long id, User requester) {
        Attachment attachment = getMetadata(id);
        boolean isOwner = attachment.getUploadedBy().getId().equals(requester.getId());
        boolean isAdmin = requester.getRole() == Role.ADMIN;
        if (!isOwner && !isAdmin) {
            throw new ForbiddenException("Ban khong co quyen tai file nay");
        }
        return attachment;
    }

    public Resource loadAsResource(Long id) {
        Attachment attachment = getMetadata(id);
        Path path = Path.of(uploadDir).toAbsolutePath().normalize().resolve(attachment.getStoredFileName()).normalize();
        if (!Files.exists(path)) {
            throw new ResourceNotFoundException("File vat ly khong ton tai");
        }
        return new FileSystemResource(path);
    }

    public void delete(Long id, User requester) {
        Attachment attachment = getMetadata(id);
        boolean isOwner = attachment.getUploadedBy().getId().equals(requester.getId());
        boolean isAdmin = requester.getRole() == Role.ADMIN;
        if (!isOwner && !isAdmin) {
            throw new ForbiddenException("Ban khong co quyen xoa file nay");
        }

        Path path = Path.of(uploadDir).toAbsolutePath().normalize().resolve(attachment.getStoredFileName()).normalize();
        try {
            Files.deleteIfExists(path);
        } catch (IOException ignored) {
        }
        attachmentRepository.delete(attachment);
    }

    private void validateFile(MultipartFile file) {
        if (file.getSize() > maxSizeBytes) {
            throw new BadRequestException("File vuot qua dung luong cho phep");
        }

        String contentType = file.getContentType();
        Set<String> allowed = Arrays.stream(allowedContentTypes.split(","))
                .map(String::trim)
                .filter(value -> !value.isBlank())
                .collect(Collectors.toSet());
        if (contentType == null || !allowed.contains(contentType)) {
            throw new BadRequestException("Dinh dang file khong duoc ho tro");
        }
    }
}

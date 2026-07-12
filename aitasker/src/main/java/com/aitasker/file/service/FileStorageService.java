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
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FileStorageService {

    private static final Set<String> ALLOWED_EXTENSIONS = Set.of(
            ".pdf", ".docx", ".doc", ".png", ".jpg", ".jpeg", ".zip");
    private static final long MAX_FILE_SIZE = 20L * 1024 * 1024;

    private final AttachmentRepository attachmentRepository;

    @Value("${app.upload.dir:uploads}")
    private String uploadDir;

    public Attachment store(MultipartFile file, User uploader) {
        if (file == null || file.isEmpty()) {
            throw new BadRequestException("File không được để trống");
        }
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new BadRequestException("File vượt quá kích thước tối đa 20MB");
        }

        String original = file.getOriginalFilename();
        String extension = extractExtension(original);
        if (!ALLOWED_EXTENSIONS.contains(extension.toLowerCase())) {
            throw new BadRequestException("Định dạng file không được hỗ trợ: " + extension);
        }

        try {
            byte[] header = readHeader(file);
            if (!matchesDeclaredType(extension.toLowerCase(), header)) {
                throw new BadRequestException("Nội dung file không khớp với định dạng khai báo (" + extension + ")");
            }

            Path dir = Path.of(uploadDir);
            Files.createDirectories(dir);

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

    private String extractExtension(String originalFileName) {
        if (originalFileName == null || !originalFileName.contains(".")) {
            return "";
        }
        return originalFileName.substring(originalFileName.lastIndexOf('.'));
    }

    private byte[] readHeader(MultipartFile file) throws IOException {
        byte[] buffer = new byte[8];
        try (var in = file.getInputStream()) {
            int read = in.read(buffer);
            return read > 0 ? java.util.Arrays.copyOf(buffer, read) : new byte[0];
        }
    }

    /**
     * Kiểm tra "magic bytes" thật của file thay vì chỉ tin vào phần mở rộng/Content-Type
     * do client gửi lên — chặn trường hợp đổi tên file .exe thành .pdf.
     */
    private boolean matchesDeclaredType(String extension, byte[] header) {
        if (header.length < 4) return false;

        return switch (extension) {
            case ".pdf" -> startsWith(header, 0x25, 0x50, 0x44, 0x46); // %PDF
            case ".png" -> startsWith(header, 0x89, 0x50, 0x4E, 0x47);
            case ".jpg", ".jpeg" -> startsWith(header, 0xFF, 0xD8, 0xFF);
            case ".zip", ".docx" -> startsWith(header, 0x50, 0x4B, 0x03, 0x04)
                    || startsWith(header, 0x50, 0x4B, 0x05, 0x06);
            case ".doc" -> startsWith(header, 0xD0, 0xCF, 0x11, 0xE0);
            default -> false;
        };
    }

    private boolean startsWith(byte[] header, int... expected) {
        if (header.length < expected.length) return false;
        for (int i = 0; i < expected.length; i++) {
            if ((header[i] & 0xFF) != expected[i]) return false;
        }
        return true;
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

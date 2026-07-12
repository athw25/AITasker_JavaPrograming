package com.aitasker.expert.service.impl;

import com.aitasker.exception.BadRequestException;
import com.aitasker.exception.ForbiddenException;
import com.aitasker.exception.ResourceNotFoundException;
import com.aitasker.expert.entity.Attachment;
import com.aitasker.expert.repository.AttachmentRepository;
import com.aitasker.expert.service.FileStorageService;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.*;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
public class FileStorageServiceImpl implements FileStorageService {

    // File Storage Security: chỉ chấp nhận các định dạng thường dùng cho
    // Portfolio/Delivery/Project Attachments — chặn file thực thi (.exe, .sh, .jar...).
    private static final Set<String> ALLOWED_CONTENT_TYPES = Set.of(
            "image/jpeg", "image/png", "image/gif", "image/webp",
            "application/pdf",
            "application/msword",
            "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
            "application/vnd.ms-excel",
            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
            "application/zip", "application/x-zip-compressed",
            "text/plain"
    );

    private static final long MAX_FILE_SIZE_BYTES = 10L * 1024 * 1024; // 10MB

    private final AttachmentRepository attachmentRepository;
    private final Path rootLocation = Paths.get("uploads");

    public FileStorageServiceImpl(AttachmentRepository attachmentRepository) {
        this.attachmentRepository = attachmentRepository;
        try {
            Files.createDirectories(rootLocation); // Tự động tạo folder vật lý 'uploads' nếu chưa có
        } catch (IOException e) {
            throw new RuntimeException("Không thể khởi tạo thư mục lưu trữ file hệ thống!");
        }
    }

    @Override
    public Attachment uploadFile(MultipartFile file, Long currentUserId) {
        if (file == null || file.isEmpty()) {
            throw new BadRequestException("File tải lên không hợp lệ hoặc trống!");
        }
        if (file.getSize() > MAX_FILE_SIZE_BYTES) {
            throw new BadRequestException("Kích thước file vượt quá giới hạn cho phép (10MB)!");
        }
        if (!ALLOWED_CONTENT_TYPES.contains(file.getContentType())) {
            throw new BadRequestException("Định dạng file không được hỗ trợ: " + file.getContentType());
        }

        try {
            // Chỉ giữ lại tên file (bỏ mọi thành phần thư mục) để chặn Path Traversal
            // (vd: originalFilename = "../../../evil.sh") trước khi ghép với UUID.
            String safeOriginalName = Paths.get(file.getOriginalFilename()).getFileName().toString();
            String uniqueFileName = UUID.randomUUID() + "_" + safeOriginalName;

            Path destinationFile = this.rootLocation.resolve(uniqueFileName).normalize().toAbsolutePath();
            if (!destinationFile.startsWith(this.rootLocation.toAbsolutePath())) {
                throw new BadRequestException("Tên file không hợp lệ!");
            }

            // Copy dòng dữ liệu vật lý vào thư mục uploads
            Files.copy(file.getInputStream(), destinationFile, StandardCopyOption.REPLACE_EXISTING);

            // Ghi nhận thực thể xuống cơ sở dữ liệu
            Attachment attachment = Attachment.builder()
                    .fileName(safeOriginalName)
                    .fileType(file.getContentType())
                    .filePath(destinationFile.toString())
                    .fileSize(file.getSize())
                    .uploadedBy(currentUserId)
                    .build();

            return attachmentRepository.save(attachment);
        } catch (IOException e) {
            throw new RuntimeException("Lỗi hệ thống khi lưu trữ file vật lý!", e);
        }
    }

    @Override
    public Resource downloadFile(Long fileId, Long currentUserId, boolean isAdmin) {
        Attachment attachment = attachmentRepository.findById(fileId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy tệp tin yêu cầu trong hệ thống!"));

        // Access control: chỉ người upload hoặc ADMIN được tải file về
        if (!isAdmin && !attachment.getUploadedBy().equals(currentUserId)) {
            throw new ForbiddenException("Bạn không có quyền truy cập tệp tin này!");
        }

        try {
            Path file = Paths.get(attachment.getFilePath());
            Resource resource = new UrlResource(file.toUri());
            if (resource.exists() || resource.isReadable()) {
                return resource;
            } else {
                throw new ResourceNotFoundException("Tập tin không tồn tại trên hệ thống lưu trữ vật lý!");
            }
        } catch (MalformedURLException e) {
            throw new BadRequestException("Đường dẫn tệp tin không hợp lệ!");
        }
    }

    @Override
    public void deleteFile(Long fileId, Long currentUserId) {
        Attachment attachment = attachmentRepository.findById(fileId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy tệp tin cần xóa!"));

        // Bảo mật Ownership: Chỉ người upload file mới được phép xóa file đó
        if (!attachment.getUploadedBy().equals(currentUserId)) {
            throw new ForbiddenException("Bạn không có quyền xóa tệp tin của người khác!");
        }

        try {
            // Xóa file vật lý ổ cứng trước
            Path file = Paths.get(attachment.getFilePath());
            Files.deleteIfExists(file);

            // Xóa bản ghi trong Database sau
            attachmentRepository.delete(attachment);
        } catch (IOException e) {
            throw new RuntimeException("Lỗi hệ thống khi dọn dẹp file vật lý!", e);
        }
    }
}

package com.aitasker.expert.service.impl;

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
import java.util.UUID;
import com.aitasker.security.file.FileSecurityService;


@Service
public class FileStorageServiceImpl implements FileStorageService {

    private final AttachmentRepository attachmentRepository;
    private final FileSecurityService fileSecurityService;
    private final Path rootLocation = Paths.get("uploads");

    public FileStorageServiceImpl(AttachmentRepository attachmentRepository, FileSecurityService fileSecurityService) {
        this.attachmentRepository = attachmentRepository;
        this.fileSecurityService = fileSecurityService;
        try {
            Files.createDirectories(rootLocation); // Tự động tạo folder vật lý 'uploads' nếu chưa có
        } catch (IOException e) {
            throw new RuntimeException("Không thể khởi tạo thư mục lưu trữ file hệ thống!");
        }
    }

    @Override
    public Attachment uploadFile(MultipartFile file, Long currentUserId) {
        fileSecurityService.validateFile(file);
        try {
            if (file.isEmpty()) {
                throw new RuntimeException("File tải lên không hợp lệ hoặc trống!");
            }
            // Chuẩn hóa tên file bằng UUID để tránh bị ghi đè khi hai người dùng upload file trùng tên
            String uniqueFileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
            Path destinationFile = this.rootLocation.resolve(Paths.get(uniqueFileName)).normalize().toAbsolutePath();

            // Copy dòng dữ liệu vật lý vào thư mục uploads
            Files.copy(file.getInputStream(), destinationFile, StandardCopyOption.REPLACE_EXISTING);

            // Ghi nhận thực thể xuống cơ sở dữ liệu
            Attachment attachment = Attachment.builder()
                    .fileName(file.getOriginalFilename())
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
    public Resource downloadFile(Long fileId) {
        Attachment attachment = attachmentRepository.findById(fileId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy tệp tin yêu cầu trong hệ thống!"));

        org.springframework.security.core.Authentication auth = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof com.aitasker.security.userdetails.CustomUserDetails userDetails) {
            com.aitasker.user.entity.User currentUser = userDetails.getUser();
            fileSecurityService.checkDownloadAccess(attachment, currentUser);
        } else {
            throw new org.springframework.security.access.AccessDeniedException("Yêu cầu đăng nhập để truy cập tệp tin!");
        }

        try {
            Path file = Paths.get(attachment.getFilePath());
            Resource resource = new UrlResource(file.toUri());
            if (resource.exists() || resource.isReadable()) {
                return resource;
            } else {
                throw new RuntimeException("Tập tin không tồn tại trên hệ thống lưu trữ vật lý!");
            }
        } catch (MalformedURLException e) {
            throw new RuntimeException("Đường dẫn tệp tin không hợp lệ!", e);
        }
    }

    @Override
    public void deleteFile(Long fileId, Long currentUserId) {
        Attachment attachment = attachmentRepository.findById(fileId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy tệp tin cần xóa!"));

        // Bảo mật Ownership: Chỉ người upload file mới được phép xóa file đó
        if (!attachment.getUploadedBy().equals(currentUserId)) {
            throw new RuntimeException("Bạn không có quyền xóa tệp tin của người khác!");
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
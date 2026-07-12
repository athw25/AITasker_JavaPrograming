package com.aitasker.security.file;

import com.aitasker.expert.entity.Attachment;
import com.aitasker.security.ProjectSecurityService;
import com.aitasker.user.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class FileSecurityService {

    private final ProjectSecurityService projectSecurityService;

    private static final List<String> ALLOWED_EXTENSIONS = Arrays.asList(
            "pdf", "docx", "doc", "png", "jpg", "jpeg", "zip", "xlsx", "xls"
    );

    private static final List<String> ALLOWED_MIME_TYPES = Arrays.asList(
            "application/pdf",
            "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
            "application/msword",
            "image/png",
            "image/jpeg",
            "image/jpg",
            "application/zip",
            "application/x-zip-compressed",
            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
            "application/vnd.ms-excel"
    );

    @Value("${file.upload.max-size:10485760}")
    private long maxFileSize;

    public void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File tải lên không hợp lệ hoặc trống!");
        }

        if (file.getSize() > maxFileSize) {
            throw new IllegalArgumentException(String.format(
                    "Kích thước file (%d bytes) vượt quá giới hạn tối đa cho phép (%d bytes)!",
                    file.getSize(), maxFileSize
            ));
        }

        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || !originalFilename.contains(".")) {
            throw new IllegalArgumentException("Tên file không hợp lệ (thiếu phần mở rộng)!");
        }

        String extension = originalFilename.substring(originalFilename.lastIndexOf(".") + 1).toLowerCase();
        if (!ALLOWED_EXTENSIONS.contains(extension)) {
            throw new IllegalArgumentException("Định dạng file không được phép tải lên! (Chỉ chấp nhận: PDF, DOC, DOCX, PNG, JPG, JPEG, ZIP, XLSX, XLS)");
        }

        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_MIME_TYPES.contains(contentType.toLowerCase())) {
            throw new IllegalArgumentException("Kiểu nội dung file (MIME type) không hợp lệ!");
        }
    }

    public void checkDownloadAccess(Attachment attachment, User currentUser) {
        if (attachment == null) {
            throw new IllegalArgumentException("Không tìm thấy thông tin tệp tin!");
        }

        if (currentUser == null) {
            throw new AccessDeniedException("Yêu cầu đăng nhập để truy cập tệp tin!");
        }

        if (currentUser.getRole().name().equals("ADMIN")) {
            return;
        }

        if (attachment.getUploadedBy().equals(currentUser.getId())) {
            return;
        }

        // File gắn với một Project (Delivery, Milestone, Project Attachment...) thì cả hai
        // phía Client và Expert của Project đó đều được phép tải xuống, không chỉ người upload.
        if (attachment.getRelatedProjectId() != null) {
            projectSecurityService.checkCanAccessProject(attachment.getRelatedProjectId(), currentUser);
            return;
        }

        throw new AccessDeniedException("Bạn không có quyền tải xuống tệp tin này!");
    }
}

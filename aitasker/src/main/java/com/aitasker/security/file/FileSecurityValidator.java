package com.aitasker.security.file;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.*;

@Component
@Slf4j
public class FileSecurityValidator {

    @Value("${app.file.max-size:10485760}")
    private long maxFileSize; // 10MB default

    @Value("${app.file.allowed-types:pdf,doc,docx,jpg,jpeg,png,gif,xlsx,xls}")
    private String allowedTypesStr;

    private static final Map<String, String> ALLOWED_MIME_TYPES = new HashMap<>();
    private static final Set<String> DANGEROUS_EXTENSIONS = new HashSet<>();

    static {
        ALLOWED_MIME_TYPES.put("pdf", "application/pdf");
        ALLOWED_MIME_TYPES.put("doc", "application/msword");
        ALLOWED_MIME_TYPES.put("docx", "application/vnd.openxmlformats-officedocument.wordprocessingml.document");
        ALLOWED_MIME_TYPES.put("jpg", "image/jpeg");
        ALLOWED_MIME_TYPES.put("jpeg", "image/jpeg");
        ALLOWED_MIME_TYPES.put("png", "image/png");
        ALLOWED_MIME_TYPES.put("gif", "image/gif");
        ALLOWED_MIME_TYPES.put("xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        ALLOWED_MIME_TYPES.put("xls", "application/vnd.ms-excel");

        DANGEROUS_EXTENSIONS.addAll(Arrays.asList(
                "exe", "bat", "cmd", "com", "pif", "scr", "vbs", "js",
                "jar", "zip", "rar", "7z", "sh", "bash", "php", "asp",
                "aspx", "jsp", "py", "pl", "cgi"
        ));
    }

    public boolean validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            log.warn("File is null or empty");
            return false;
        }

        if (!validateFileSize(file)) {
            return false;
        }

        if (!validateFileType(file)) {
            return false;
        }

        if (!validateFileName(file)) {
            return false;
        }

        return true;
    }

    public boolean validateFileSize(MultipartFile file) {
        if (file.getSize() > maxFileSize) {
            log.warn("File size {} exceeds maximum allowed size {}", file.getSize(), maxFileSize);
            return false;
        }
        return true;
    }

    public boolean validateFileType(MultipartFile file) {
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null) {
            log.warn("File name is null");
            return false;
        }

        String fileExtension = getFileExtension(originalFilename).toLowerCase();

        if (!isAllowedExtension(fileExtension)) {
            log.warn("File extension {} is not allowed", fileExtension);
            return false;
        }

        String contentType = file.getContentType();
        if (contentType != null && !isAllowedMimeType(fileExtension, contentType)) {
            log.warn("MIME type {} for extension {} is not allowed", contentType, fileExtension);
            return false;
        }

        return true;
    }

    public boolean validateFileName(MultipartFile file) {
        String filename = file.getOriginalFilename();
        if (filename == null) {
            return false;
        }

        if (filename.contains("..") || filename.contains("/") || filename.contains("\\")) {
            log.warn("Filename contains path traversal characters");
            return false;
        }

        if (filename.length() > 255) {
            log.warn("Filename too long: {}", filename.length());
            return false;
        }

        return true;
    }

    public boolean isAllowedExtension(String extension) {
        return ALLOWED_MIME_TYPES.containsKey(extension) && !DANGEROUS_EXTENSIONS.contains(extension);
    }

    public boolean isAllowedMimeType(String extension, String mimeType) {
        String expected = ALLOWED_MIME_TYPES.get(extension);
        return expected != null && expected.equals(mimeType);
    }

    public String getFileExtension(String filename) {
        if (filename == null || !filename.contains(".")) {
            return "";
        }
        return filename.substring(filename.lastIndexOf(".") + 1);
    }

    public String sanitizeFileName(String filename) {
        return filename.replaceAll("[^a-zA-Z0-9._-]", "_");
    }

    public byte[] validateAndExtractFileContent(MultipartFile file) throws IOException {
        if (!validateFile(file)) {
            throw new IllegalArgumentException("File validation failed");
        }
        return file.getBytes();
    }
}


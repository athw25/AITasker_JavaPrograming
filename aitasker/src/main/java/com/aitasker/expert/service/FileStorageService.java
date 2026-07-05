package com.aitasker.expert.service;

import com.aitasker.expert.entity.Attachment;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.core.io.Resource;

public interface FileStorageService {
    Attachment uploadFile(MultipartFile file, Long currentUserId);
    Resource downloadFile(Long fileId);
    void deleteFile(Long fileId, Long currentUserId);
}
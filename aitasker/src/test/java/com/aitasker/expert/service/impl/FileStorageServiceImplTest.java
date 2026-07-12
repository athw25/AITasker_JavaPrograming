package com.aitasker.expert.service.impl;

import com.aitasker.exception.BadRequestException;
import com.aitasker.exception.ForbiddenException;
import com.aitasker.exception.ResourceNotFoundException;
import com.aitasker.expert.entity.Attachment;
import com.aitasker.expert.repository.AttachmentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FileStorageServiceImplTest {

    @Mock
    private AttachmentRepository attachmentRepository;

    private FileStorageServiceImpl fileStorageService;

    @BeforeEach
    void setUp() {
        fileStorageService = new FileStorageServiceImpl(attachmentRepository);
    }

    @Test
    void uploadFile_dinhDangKhongDuocPhep_biTuChoi() {
        MockMultipartFile file = new MockMultipartFile(
                "file", "malware.exe", "application/x-msdownload", "fake-binary".getBytes());

        assertThatThrownBy(() -> fileStorageService.uploadFile(file, 1L))
                .isInstanceOf(BadRequestException.class);
    }

    @Test
    void uploadFile_vuotKichThuocChoPhep_biTuChoi() {
        byte[] oversized = new byte[11 * 1024 * 1024]; // 11MB > giới hạn 10MB
        MockMultipartFile file = new MockMultipartFile(
                "file", "big.png", "image/png", oversized);

        assertThatThrownBy(() -> fileStorageService.uploadFile(file, 1L))
                .isInstanceOf(BadRequestException.class);
    }

    @Test
    void uploadFile_tenFileChuaPathTraversal_duocLamSachTruocKhiLuu() {
        MockMultipartFile file = new MockMultipartFile(
                "file", "../../../etc/evil.png", "image/png", "content".getBytes());

        when(attachmentRepository.save(any(Attachment.class))).thenAnswer(inv -> inv.getArgument(0));

        Attachment saved = fileStorageService.uploadFile(file, 1L);

        assertThat(saved.getFileName()).isEqualTo("evil.png");
        assertThat(saved.getFilePath()).doesNotContain("..");
    }

    @Test
    void uploadFile_hopLe_luuThanhCong() {
        MockMultipartFile file = new MockMultipartFile(
                "file", "resume.pdf", "application/pdf", "content".getBytes());

        when(attachmentRepository.save(any(Attachment.class))).thenAnswer(inv -> inv.getArgument(0));

        Attachment saved = fileStorageService.uploadFile(file, 1L);

        assertThat(saved.getUploadedBy()).isEqualTo(1L);
        assertThat(saved.getFileType()).isEqualTo("application/pdf");
    }

    @Test
    void downloadFile_nguoiNgoaiCuoc_biTuChoi() {
        Attachment attachment = Attachment.builder().id(5L).uploadedBy(1L).filePath("uploads/x").build();
        when(attachmentRepository.findById(5L)).thenReturn(Optional.of(attachment));

        assertThatThrownBy(() -> fileStorageService.downloadFile(5L, 2L, false))
                .isInstanceOf(ForbiddenException.class);
    }

    @Test
    void deleteFile_khongPhaiChuSoHuu_biTuChoi() {
        Attachment attachment = Attachment.builder().id(5L).uploadedBy(1L).filePath("uploads/x").build();
        when(attachmentRepository.findById(5L)).thenReturn(Optional.of(attachment));

        assertThatThrownBy(() -> fileStorageService.deleteFile(5L, 2L))
                .isInstanceOf(ForbiddenException.class);
    }

    @Test
    void downloadFile_fileKhongTonTai_neResourceNotFound() {
        when(attachmentRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> fileStorageService.downloadFile(99L, 1L, false))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}

package com.aitasker.proposal.controller;

import com.aitasker.common.response.ApiResponse;
import com.aitasker.common.response.PageResponse;
import com.aitasker.proposal.dto.request.ProposalRequestDTO;
import com.aitasker.proposal.dto.response.ProposalResponseDTO;
import com.aitasker.proposal.service.ProposalService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/proposals")
@RequiredArgsConstructor
public class ProposalController {

    private final ProposalService proposalService;

    // API 1: Nộp đề xuất mới
    @PostMapping
    public ApiResponse<ProposalResponseDTO> createProposal(
            @Valid @RequestBody ProposalRequestDTO request,
            @RequestParam Long expertId) {
        ProposalResponseDTO response = proposalService.createProposal(request, expertId);
        return ApiResponse.success("Nộp đề xuất thành công", response);
    }

    // API 2: Xem danh sách đề xuất của một công việc
    @GetMapping("/job/{jobId}")
    public ApiResponse<PageResponse<ProposalResponseDTO>> getProposalsByJob(
            @PathVariable Long jobId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        PageResponse<ProposalResponseDTO> response = proposalService.getProposalsByJob(jobId, page, size);
        return ApiResponse.success("Lấy danh sách đề xuất thành công", response);
    }

    // API 3: Client chấp nhận đề xuất
    @PutMapping("/{id}/accept")
    public ApiResponse<String> acceptProposal(
            @PathVariable Long id,
            @RequestParam Long clientId) {
        proposalService.acceptProposal(id, clientId);
        return ApiResponse.success("Chấp nhận đề xuất thành công", null);
    }

    // API 4: Client từ chối đề xuất
    @PutMapping("/{id}/reject")
    public ApiResponse<String> rejectProposal(
            @PathVariable Long id,
            @RequestParam Long clientId) {
        proposalService.rejectProposal(id, clientId);
        return ApiResponse.success("Từ chối đề xuất thành công", null);
    }

    // API 5: Expert rút lại đề xuất
    @PutMapping("/{id}/withdraw")
    public ApiResponse<String> withdrawProposal(
            @PathVariable Long id,
            @RequestParam Long expertId) {
        proposalService.withdrawProposal(id, expertId);
        return ApiResponse.success("Rút đề xuất thành công", null);
    }
}
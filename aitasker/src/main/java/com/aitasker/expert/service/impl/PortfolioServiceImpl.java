package com.aitasker.expert.service.impl;

import com.aitasker.expert.dto.request.CreatePortfolioRequest;
import com.aitasker.expert.dto.response.PortfolioResponse;
import com.aitasker.expert.entity.Portfolio;
import com.aitasker.expert.repository.PortfolioRepository;
import com.aitasker.expert.service.PortfolioService;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PortfolioServiceImpl implements PortfolioService {

    private final PortfolioRepository portfolioRepository;

    public PortfolioServiceImpl(PortfolioRepository portfolioRepository) {
        this.portfolioRepository = portfolioRepository;
    }

    @Override
    public PortfolioResponse addPortfolio(Long currentUserId, CreatePortfolioRequest request) {
        Portfolio portfolio = new Portfolio();
        portfolio.setExpertId(currentUserId);
        portfolio.setProjectName(request.getProjectName());
        portfolio.setDescription(request.getDescription());
        portfolio.setProjectUrl(request.getProjectUrl());

        Portfolio saved = portfolioRepository.save(portfolio);
        return convertToResponse(saved);
    }

    @Override
    public List<PortfolioResponse> getPortfoliosByExpert(Long expertId) {
        return portfolioRepository.findByExpertId(expertId).stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public void deletePortfolio(Long currentUserId, Long portfolioId) {
        Portfolio portfolio = portfolioRepository.findById(portfolioId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy dự án portfolio cần xóa!"));
        
        // Kiểm tra quyền sở hữu: Chỉ chính expert tạo ra mới được quyền xóa portfolio đó
        if (!portfolio.getExpertId().equals(currentUserId)) {
            throw new RuntimeException("Bạn không có quyền xóa hồ sơ năng lực của người khác!");
        }
        portfolioRepository.deleteById(portfolioId);
    }

    private PortfolioResponse convertToResponse(Portfolio entity) {
        PortfolioResponse res = new PortfolioResponse();
        res.setId(entity.getId());
        res.setExpertId(entity.getExpertId());
        res.setProjectName(entity.getProjectName());
        res.setDescription(entity.getDescription());
        res.setProjectUrl(entity.getProjectUrl());
        return res;
    }
}
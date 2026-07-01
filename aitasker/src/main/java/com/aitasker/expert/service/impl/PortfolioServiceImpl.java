package com.aitasker.expert.service.impl;

import com.aitasker.expert.dto.request.CreatePortfolioRequest;
import com.aitasker.expert.dto.response.PortfolioResponse;
import com.aitasker.expert.entity.Portfolio;
import com.aitasker.expert.exception.ExpertNotFoundException;
import com.aitasker.expert.repository.PortfolioRepository;
import com.aitasker.expert.service.PortfolioService;
import com.aitasker.user.entity.User;
import com.aitasker.user.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PortfolioServiceImpl implements PortfolioService {

    private final PortfolioRepository portfolioRepository;
    private final UserRepository userRepository;

    public PortfolioServiceImpl(PortfolioRepository portfolioRepository,
                                UserRepository userRepository) {
        this.portfolioRepository = portfolioRepository;
        this.userRepository = userRepository;
    }

    @Override
    public PortfolioResponse addPortfolio(Long currentUserId, CreatePortfolioRequest request) {

        User expert = userRepository.findById(currentUserId)
                .orElseThrow(() -> new ExpertNotFoundException("Không tìm thấy chuyên gia!"));

        Portfolio portfolio = new Portfolio();
        portfolio.setExpert(expert);
        portfolio.setProjectName(request.getProjectName());
        portfolio.setDescription(request.getDescription());
        portfolio.setProjectUrl(request.getProjectUrl());

        Portfolio saved = portfolioRepository.save(portfolio);

        return convertToResponse(saved);
    }

    @Override
    public List<PortfolioResponse> getPortfoliosByExpert(Long expertId) {

        return portfolioRepository.findByExpert_Id(expertId)
                .stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public void deletePortfolio(Long currentUserId, Long portfolioId) {

        Portfolio portfolio = portfolioRepository.findById(portfolioId)
                .orElseThrow(() ->
                        new RuntimeException("Không tìm thấy dự án Portfolio cần xóa!"));

        if (!portfolio.getExpert().getId().equals(currentUserId)) {
            throw new ExpertNotFoundException(
                    "Bạn không có quyền xóa Portfolio của người khác!");
        }

        portfolioRepository.delete(portfolio);
    }

    private PortfolioResponse convertToResponse(Portfolio entity) {

        PortfolioResponse res = new PortfolioResponse();

        res.setId(entity.getId());
        res.setExpertId(entity.getExpert().getId());
        res.setProjectName(entity.getProjectName());
        res.setDescription(entity.getDescription());
        res.setProjectUrl(entity.getProjectUrl());

        return res;
    }
}
package com.aitasker.expert.service;

import com.aitasker.expert.dto.request.CreatePortfolioRequest;
import com.aitasker.expert.dto.response.PortfolioResponse;
import java.util.List;

public interface PortfolioService {
    PortfolioResponse addPortfolio(Long currentUserId, CreatePortfolioRequest request);
    List<PortfolioResponse> getPortfoliosByExpert(Long expertId);
    void deletePortfolio(Long currentUserId, Long portfolioId);
}
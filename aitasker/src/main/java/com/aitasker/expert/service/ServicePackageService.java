package com.aitasker.expert.service;

import com.aitasker.expert.dto.request.CreateServicePackageRequest;
import com.aitasker.expert.dto.response.ServicePackageResponse;
import java.util.List;

public interface ServicePackageService {
    ServicePackageResponse createPackage(Long currentUserId, CreateServicePackageRequest request);
    List<ServicePackageResponse> getAllPackages();
    ServicePackageResponse updatePackage(Long currentUserId, Long packageId, com.aitasker.expert.dto.request.UpdateServicePackageRequest request);
void deletePackage(Long currentUserId, Long packageId);
}
package com.biopix.telemedicine_backend.admin.internal.service;

import com.biopix.telemedicine_backend.admin.internal.dto.AdminBasicDetailsResponse;
import com.biopix.telemedicine_backend.admin.internal.dto.AdminFullDetailsResponse;
import com.biopix.telemedicine_backend.admin.internal.dto.AdminInsertProfileDetailsRequest;
import com.biopix.telemedicine_backend.admin.internal.dto.AdminInsertProfileDetailsResponse;
import com.biopix.telemedicine_backend.admin.internal.entity.Admin;
import com.biopix.telemedicine_backend.admin.internal.exceptions.AdminNotFoundException;
import com.biopix.telemedicine_backend.admin.internal.exceptions.AdminProfileAlreadyExistsException;
import com.biopix.telemedicine_backend.admin.internal.repo.AdminRepository;
import com.biopix.telemedicine_backend.shared.contracts.events.ProfileCompletedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class AdminService {
    private final AdminRepository adminRepository;
    private final ApplicationEventPublisher applicationEventPublisher;

    @Transactional
    public AdminInsertProfileDetailsResponse saveAdminProfile(Long accountId, String email, AdminInsertProfileDetailsRequest request) {

        if (adminRepository.existsByEmail(email)) {
            throw new AdminProfileAlreadyExistsException();
        }

        Admin admin = Admin.builder()
                .accountId(accountId)
                .firstName(request.firstName())
                .lastName(request.lastName())
                .email(email)
                .phoneNumber(request.phoneNumber())
                .department(request.department())
                .build();

        Admin savedAdmin = adminRepository.save(admin);

        //Publish event for profile creation
        applicationEventPublisher.publishEvent(
                new ProfileCompletedEvent(accountId, email, "ADMIN")
        );

        return new AdminInsertProfileDetailsResponse(
                savedAdmin.getId(),
                savedAdmin.getEmail(),
                savedAdmin.getFirstName(),
                savedAdmin.getLastName(),
                savedAdmin.getDepartment()
        );
    }


    public AdminBasicDetailsResponse getBasicDetails(Long id) {

        Admin admin = adminRepository.findByAccountId(id);

        if (admin == null) {
            throw new AdminNotFoundException();
        }

        AdminBasicDetailsResponse response = new AdminBasicDetailsResponse(
                admin.getId(),
                admin.getEmail(),
                admin.getFirstName(),
                admin.getLastName(),
                admin.getDepartment()
        );

        return response;

    }

    public AdminFullDetailsResponse getFullDetails(Long id) {

        Admin admin = adminRepository.findByAccountId(id);

        if (admin == null) {
            throw new AdminNotFoundException();
        }

        AdminFullDetailsResponse response = new AdminFullDetailsResponse(
                admin.getId(),
                admin.getFirstName(),
                admin.getLastName(),
                admin.getEmail(),
                admin.getPhoneNumber(),
                admin.getDepartment(),
                admin.getCreatedAt(),
                admin.getUpdatedAt()
        );

        return response;
    }
}

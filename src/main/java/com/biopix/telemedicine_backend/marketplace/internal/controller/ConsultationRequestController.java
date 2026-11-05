package com.biopix.telemedicine_backend.marketplace.internal.controller;

import com.biopix.telemedicine_backend.payment.PaymentService;
import com.biopix.telemedicine_backend.shared.security.AuthenticatedUser;
import com.biopix.telemedicine_backend.marketplace.internal.dto.*;
import com.biopix.telemedicine_backend.marketplace.internal.enums.ConsultationRequestStatus;
import com.biopix.telemedicine_backend.marketplace.internal.enums.ConsultationRequestType;
import com.biopix.telemedicine_backend.marketplace.internal.enums.SortByStatus;
import com.biopix.telemedicine_backend.marketplace.internal.service.ConsultationRequestService;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Stream;


@RequiredArgsConstructor
@RequestMapping("/api/marketplace/request")
@RestController
public class ConsultationRequestController {

    private final ConsultationRequestService requestService;
    private final PaymentService paymentService;

    @PostMapping("/create")
    @PreAuthorize("hasRole('PATIENT') and hasAuthority('FULL_ACCESS')")
    public ResponseEntity<CreateConsultationRequestResponse> create(
            @AuthenticationPrincipal AuthenticatedUser authenticatedUser,
            @RequestBody ConsultationRequestDto request
    ){

        Long id =  authenticatedUser.getUserId();
        String email = authenticatedUser.getUsername();
        String role = authenticatedUser.getRole();

        if(!paymentService.hasPaymentAccount(id, role)){
            String setupUrl = paymentService.createPaymentAccountSetup(id, role);

            return ResponseEntity.unprocessableContent()
                    .body(
                            new CreateConsultationRequestResponse(
                                    null,
                                    setupUrl
                            ));
        }

        ConsultationRequestResponse response =
                requestService.create(
                        id,
                        email,
                        request
                );


        return ResponseEntity.ok().body(
                new CreateConsultationRequestResponse(
                        response,
                        null
        ));

    }


    @GetMapping("/my-requests")
    @PreAuthorize("hasRole('PATIENT') and hasAuthority('FULL_ACCESS')")
    public ResponseEntity<List<ConsultationRequestDetails>> getMyMadeRequests(
            @AuthenticationPrincipal AuthenticatedUser authenticatedUser,
            @RequestParam(required = false) ConsultationRequestStatus status

    ){
        Long id =  authenticatedUser.getUserId();

        return  ResponseEntity.ok().body(requestService.findAllRelatedRequestByStatus(id , status));
    }


    @GetMapping("/consultation-request-statuses")
    public List<String> getConsultationRequestStatus(){

        return Stream.of(ConsultationRequestStatus.values())
                .map(ConsultationRequestStatus::name)
                .toList();
    }

    @GetMapping("/consultation-request-types")
    public List<String> getConsultationRequestType(){

        return Stream.of(ConsultationRequestType.values())
                .map(ConsultationRequestType::name)
                .toList();
    }

    @GetMapping("/sort-by-statuses")
    public List<String> getSortByStatus(){

        return Stream.of(SortByStatus.values())
                .map(SortByStatus::name)
                .toList();
    }



    @GetMapping("/all-open")
    @PreAuthorize("hasRole('DOCTOR') and hasAuthority('FULL_ACCESS')")
    public  ResponseEntity<List<ConsultationRequestBasicDetails>> getAllOpenRequests(
            @ModelAttribute ConsultationRequestFilterDetails filterDetails
    ){

        return ResponseEntity.ok().body(requestService.getAllOpenConsultations(filterDetails));
    }

    @PutMapping("/{requestId}/cancel")
    @PreAuthorize("hasRole('PATIENT') and hasAuthority('FULL_ACCESS')")
    public ResponseEntity<ConsultationRequestResponse> cancelRequest(
            @AuthenticationPrincipal AuthenticatedUser authenticatedUser,
            @PathVariable Long requestId
    ) {
        Long id = authenticatedUser.getUserId();

        ConsultationRequestResponse response =
                requestService.cancelRequest(id, requestId);

        return ResponseEntity.ok(response);
    }


}

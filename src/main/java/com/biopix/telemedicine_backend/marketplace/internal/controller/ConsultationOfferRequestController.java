package com.biopix.telemedicine_backend.marketplace.internal.controller;

import com.biopix.telemedicine_backend.marketplace.internal.dto.CreateDoctorOfferResponse;
import com.biopix.telemedicine_backend.payment.PaymentService;
import com.biopix.telemedicine_backend.shared.security.AuthenticatedUser;
import com.biopix.telemedicine_backend.marketplace.internal.dto.AcceptedOfferResponse;
import com.biopix.telemedicine_backend.marketplace.internal.dto.DoctorOfferRequest;
import com.biopix.telemedicine_backend.marketplace.internal.dto.DoctorOfferResponse;
import com.biopix.telemedicine_backend.marketplace.internal.enums.DoctorOfferStatus;
import com.biopix.telemedicine_backend.marketplace.internal.service.DoctorOfferService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Stream;

@RequiredArgsConstructor
@RequestMapping("/api/marketplace/offer")
@RestController
public class ConsultationOfferRequestController {

    private final DoctorOfferService doctorOfferService;
    private final PaymentService paymentService;


    @PostMapping("/create")
    @PreAuthorize("hasRole('DOCTOR') and hasAuthority('FULL_ACCESS')")
    public ResponseEntity<CreateDoctorOfferResponse> createDoctorOffer(
            @AuthenticationPrincipal AuthenticatedUser authenticatedUser,
            @RequestBody DoctorOfferRequest doctorOfferRequest
    ){
        Long id = authenticatedUser.getUserId();
        String email = authenticatedUser.getUsername();
        String role = authenticatedUser.getRole();

        if (!paymentService.hasPaymentAccount(id, role)) {

            String setupUrl =
                    paymentService.createPaymentAccountSetup(id , role);

            return ResponseEntity.unprocessableContent()
                    .body(
                            new CreateDoctorOfferResponse(
                                    null,
                                    setupUrl
                            )
                    );
        }

        DoctorOfferResponse offer =
                doctorOfferService.create(
                        id,
                        email,
                        doctorOfferRequest
                );

        return ResponseEntity.ok(
                new CreateDoctorOfferResponse(
                        offer,
                        null
                )
        );
    }


    @GetMapping("/my-offers")
    @PreAuthorize("hasRole('DOCTOR') and hasAuthority('FULL_ACCESS')")
    public ResponseEntity<List<DoctorOfferResponse>> getMyMadeOffers(
            @AuthenticationPrincipal AuthenticatedUser authenticatedUser,
            @RequestParam DoctorOfferStatus status
    ){
        String email = authenticatedUser.getUsername();

        List<DoctorOfferResponse> myOffers = doctorOfferService.getOffers(email, status);

        return ResponseEntity.ok().body(myOffers);
    }



    @GetMapping("/statuses")
    public List<String> getConsultationRequestType(){

        return Stream.of(DoctorOfferStatus.values())
                .map(DoctorOfferStatus::name)
                .toList();
    }


// PATIENT ROLE scope-----------------------------------------------------

    @PostMapping("/{offerId}/accept")
    @PreAuthorize("hasRole('PATIENT') and hasAuthority('FULL_ACCESS')")
    public ResponseEntity<AcceptedOfferResponse> acceptOffer(
            @AuthenticationPrincipal AuthenticatedUser authenticatedUser,
            @PathVariable Long offerId
    ) {
        Long id = authenticatedUser.getUserId();

        AcceptedOfferResponse  response = doctorOfferService.acceptOffer(id, offerId);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/request/{requestId}")
    @PreAuthorize("hasRole('PATIENT') and hasAuthority('FULL_ACCESS')")
    public ResponseEntity<List<DoctorOfferResponse>> getOffersForRequest(
            @AuthenticationPrincipal AuthenticatedUser authenticatedUser,
            @PathVariable Long requestId
    ) {
        Long id  = authenticatedUser.getUserId();

        List<DoctorOfferResponse> response = doctorOfferService.getOffersForPatientRequest(id, requestId);

        return ResponseEntity.ok(response);
    }
// -----------------------------------------------------


    @PutMapping("/{offerId}/withdraw")
    @PreAuthorize("hasRole('DOCTOR') and hasAuthority('FULL_ACCESS')")
    public ResponseEntity<DoctorOfferResponse> withdrawOffer(
            @AuthenticationPrincipal AuthenticatedUser authenticatedUser,
            @PathVariable Long offerId
    ) {
        Long id = authenticatedUser.getUserId();

        DoctorOfferResponse response =
                doctorOfferService.withdrawOffer(id, offerId);

        return ResponseEntity.ok(response);
    }
}

package com.biopix.telemedicine_backend.marketplace.internal.service;


import com.biopix.telemedicine_backend.marketplace.internal.dto.AcceptedOfferResponse;
import com.biopix.telemedicine_backend.marketplace.internal.dto.DoctorOfferRequest;
import com.biopix.telemedicine_backend.marketplace.internal.dto.DoctorOfferResponse;
import com.biopix.telemedicine_backend.marketplace.internal.entity.ConsultationRequest;
import com.biopix.telemedicine_backend.marketplace.internal.entity.ConsultationOffer;
import com.biopix.telemedicine_backend.marketplace.internal.enums.ConsultationRequestStatus;
import com.biopix.telemedicine_backend.marketplace.internal.enums.ConsultationRequestType;
import com.biopix.telemedicine_backend.marketplace.internal.enums.DoctorOfferStatus;
import com.biopix.telemedicine_backend.marketplace.internal.exceptions.*;
import com.biopix.telemedicine_backend.marketplace.internal.repo.ConsultationRequestRepository;
import com.biopix.telemedicine_backend.marketplace.internal.repo.ConsultationOfferRepository;
import com.biopix.telemedicine_backend.shared.contracts.events.OfferAcceptedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@RequiredArgsConstructor
@Service
public class DoctorOfferService {

    private final ConsultationOfferRepository offerRepository;
    private final ConsultationRequestRepository requestRepository;
    private final ApplicationEventPublisher eventPublisher;

    public DoctorOfferResponse create(Long id,String email, DoctorOfferRequest request){

        boolean pendingOffer = offerRepository.existsByRequestIdAndDoctorAccountId(request.requestId(), id);
        if(pendingOffer ){
            throw new DoctorOfferAlreadyExistsException();
        }


        //Find Consultation Request
        ConsultationRequest consultationRequest = requestRepository.findById(request.requestId())
                .orElseThrow(ConsultationRequestNotFoundException::new);

        Instant finalProposedStartTime;

        if (consultationRequest.getRequestType() == ConsultationRequestType.NOW) {
            finalProposedStartTime = Instant.now();
        }
        else {
            if(request.proposedStartTime() == null){
                throw new InvalidProposedTimeException("Proposed start time is mandatory for SOON request types.");
            }

            if(request.proposedStartTime().isBefore(Instant.now())){
                throw new InvalidProposedTimeException("Proposed start time is in past invalid time.");
            }

            if(request.proposedStartTime().isBefore(consultationRequest.getPreferredStartTime())){
                throw new InvalidProposedTimeException("Proposed start time cannot be before the patient's preferred start time.");
            }

            finalProposedStartTime = request.proposedStartTime();
        }

        ConsultationOffer consultationOffer = ConsultationOffer.builder()
                .request(consultationRequest)
                .doctorAccountId(id)
                .doctorEmail(email)
                .price(request.price())
                .proposedStartTime(finalProposedStartTime)
                .estimatedDurationMinutes(request.estimatedDurationMinutes())
                .message(request.message())
                .build();


        ConsultationOffer savedOffer = offerRepository.save(consultationOffer);

        DoctorOfferResponse response = new DoctorOfferResponse(
                savedOffer.getId(),
                savedOffer.getRequest().getId(),
                savedOffer.getPrice(),
                savedOffer.getProposedStartTime(),
                savedOffer.getStatus(),
                savedOffer.getCreatedAt()
        );

        return response;

    }


    @Transactional
    public AcceptedOfferResponse acceptOffer(Long id, Long offerId) {

        Instant now = Instant.now();

        ConsultationOffer selectedOffer = offerRepository.findById(offerId)
                .orElseThrow(DoctorOfferNotFoundException::new);

        ConsultationRequest request = selectedOffer.getRequest();

        if (!request.getPatientAccountId().equals(id)) {
            throw new UnauthorizedConsultationRequestAccessException();
        }

        if (!request.getStatus().equals(ConsultationRequestStatus.OPEN)) {
            throw new InvalidConsultationRequestStateException("Consultation request is no longer open");
        }

        if (!selectedOffer.getStatus().equals(DoctorOfferStatus.PENDING)) {
            throw new InvalidDoctorOfferStateException("Offer is no longer pending");
        }

        Instant consultationStartTime;

        if(request.getRequestType() == ConsultationRequestType.SOON){

            if (selectedOffer.getProposedStartTime() == null) {
                throw new InvalidProposedTimeException("Proposed start time is mandatory for SOON request types.");
            }

            if(selectedOffer.getProposedStartTime().isBefore(now)){
                throw new InvalidProposedTimeException("Proposed start time has already pasted.");
            }

            consultationStartTime = selectedOffer.getProposedStartTime();

        }else {
            consultationStartTime = now;
        }

        selectedOffer.setStatus(DoctorOfferStatus.ACCEPTED);

        List<ConsultationOffer> otherOffers = offerRepository.findByRequestIdAndStatus(request.getId(), DoctorOfferStatus.PENDING);

        for (ConsultationOffer offer : otherOffers) {
            if (!offer.getId().equals(selectedOffer.getId())) {
                offer.setStatus(DoctorOfferStatus.REJECTED);
            }
        }

        request.setStatus(ConsultationRequestStatus.FULFILLED);
        request.setFulfilledAt(now);

        //Publish event using request type and proposed time
        eventPublisher.publishEvent(new OfferAcceptedEvent(
                request.getId(),
                selectedOffer.getId(),

                request.getPatientAccountId(),
                selectedOffer.getDoctorAccountId(),

                request.getPatientEmail(),
                selectedOffer.getDoctorEmail(),

                selectedOffer.getPrice(),
                request.getRequestType().name(),
                consultationStartTime,
                selectedOffer.getEstimatedDurationMinutes(),

                now


        ));

        return new AcceptedOfferResponse(
                request.getId(),
                selectedOffer.getId(),
                request.getPatientEmail(),
                selectedOffer.getDoctorEmail(),
                selectedOffer.getPrice(),
                consultationStartTime,
                selectedOffer.getEstimatedDurationMinutes(),
                selectedOffer.getStatus(),
                request.getStatus(),
                request.getFulfilledAt()
        );
    }




    public List<DoctorOfferResponse> getOffers(String email, DoctorOfferStatus status) {
        List<ConsultationOffer> offers = (status == null)
                ? offerRepository.findByDoctorEmail(email)
                : offerRepository.findByDoctorEmailAndStatus(email, status);

        return offers.stream().map(this::convertToOfferResponse).toList();
    }




    private DoctorOfferResponse convertToOfferResponse(ConsultationOffer doctorOffer){

        if(doctorOffer == null){
            return null;
        }

        return new DoctorOfferResponse(
                doctorOffer.getId(),
                doctorOffer.getRequest().getId(),
                doctorOffer.getPrice(),
                doctorOffer.getProposedStartTime(),
                doctorOffer.getStatus(),
                doctorOffer.getCreatedAt()
        );
    }


    public List<DoctorOfferResponse> getOffersForPatientRequest(Long id, Long requestId) {

        ConsultationRequest request = requestRepository.findById(requestId)
                .orElseThrow(ConsultationRequestNotFoundException::new);

        if (!request.getPatientAccountId().equals(id)) {
            throw new UnauthorizedConsultationRequestAccessException();
        }

        List<ConsultationOffer> offers = offerRepository.findByRequestId(requestId);

        return offers.stream()
                .map(this::convertToOfferResponse)
                .toList();
    }


    public DoctorOfferResponse withdrawOffer(Long id, Long offerId) {

        ConsultationOffer offer = offerRepository.findById(offerId)
                .orElseThrow(DoctorOfferNotFoundException::new);

        if (!offer.getDoctorAccountId().equals(id)) {
            throw new UnauthorizedDoctorOfferAccessException("You are not authorized to withdraw this offer");
        }

        if (!offer.getStatus().equals(DoctorOfferStatus.PENDING)) {
            throw new InvalidDoctorOfferStateException("Only pending offers can be withdrawn");
        }

        offer.setStatus(DoctorOfferStatus.WITHDRAWN);

        ConsultationOffer savedOffer = offerRepository.save(offer);

        return convertToOfferResponse(savedOffer);
    }

}

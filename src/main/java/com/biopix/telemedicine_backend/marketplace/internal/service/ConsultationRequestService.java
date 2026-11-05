package com.biopix.telemedicine_backend.marketplace.internal.service;

import com.biopix.telemedicine_backend.marketplace.internal.dto.*;
import com.biopix.telemedicine_backend.marketplace.internal.entity.ConsultationRequest;
import com.biopix.telemedicine_backend.marketplace.internal.entity.ConsultationOffer;
import com.biopix.telemedicine_backend.marketplace.internal.enums.ConsultationRequestStatus;
import com.biopix.telemedicine_backend.marketplace.internal.enums.ConsultationRequestType;
import com.biopix.telemedicine_backend.marketplace.internal.enums.DoctorOfferStatus;
import com.biopix.telemedicine_backend.marketplace.internal.enums.SortByStatus;
import com.biopix.telemedicine_backend.marketplace.internal.exceptions.*;
import com.biopix.telemedicine_backend.marketplace.internal.repo.ConsultationRequestRepository;
import com.biopix.telemedicine_backend.marketplace.internal.repo.ConsultationOfferRepository;
import com.biopix.telemedicine_backend.shared.contracts.events.RequestCreatedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;


@RequiredArgsConstructor
@Service
public class ConsultationRequestService {

    private final ConsultationRequestRepository requestRepository;
    private final ConsultationOfferRepository offerRepository;
    private final ApplicationEventPublisher publisher;

    public ConsultationRequestResponse create(Long id, String email, ConsultationRequestDto request) {

        //Check for active requests
        boolean hasActiveRequest = requestRepository.existsByPatientAccountIdAndStatus(id, ConsultationRequestStatus.OPEN);
        if (hasActiveRequest) {
            throw new ActiveConsultationRequestAlreadyExistsException();
        }

        ConsultationRequest consultationRequest = ConsultationRequest.builder()
                .patientAccountId(id)
                .patientEmail(email)
                .specialityId(request.specialityId())
                .issueDescription(request.issueDescription())
                .requestType(request.requestType())
                .preferredStartTime(
                        request.requestType().equals(ConsultationRequestType.NOW)
                                ? null
                                : request.preferredStartTime()
                )
                .expiresAt(request.preferredStartTime() == null
                        ? Instant.now().plus(1, ChronoUnit.HOURS)
                        : Instant.now().plus(5, ChronoUnit.HOURS)
                )
                .build();

        ConsultationRequest savedRequest = requestRepository.save(consultationRequest);

        ConsultationRequestResponse response = new ConsultationRequestResponse(
                savedRequest.getId(),
                savedRequest.getPatientEmail(),
                savedRequest.getPreferredStartTime(),
                savedRequest.getStatus(),
                savedRequest.getCreatedAt()
        );

        //Publish Request Created Event
        RequestCreatedEvent event  = new RequestCreatedEvent(
                savedRequest.getId(),
                savedRequest.getSpecialityId(),
                savedRequest.getRequestType().name(),
                savedRequest.getCreatedAt()
        );
        publisher.publishEvent(event);

        return response;


    }


    public List<ConsultationRequestDetails> findAllRelatedRequestByStatus(Long id, ConsultationRequestStatus status) {

        List<ConsultationRequest> requests = requestRepository.findAllByPatientAccountId(id);

        return requests.stream()
                .filter(request -> status == null || request.getStatus().equals(status))
                .map(this::convertRequestToDetails)
                .toList();
    }

    public List<ConsultationRequestBasicDetails> getAllOpenConsultations(
            ConsultationRequestFilterDetails filterDetails
    ) {
        if (filterDetails == null || filterDetails.specialityId() == null) {
            throw new MissingSpecialityException();
        }

        List<ConsultationRequest> requestDetails =
                requestRepository.findAllByStatusAndSpecialityId(
                        ConsultationRequestStatus.OPEN,
                        filterDetails.specialityId()
                );

        SortByStatus sortBy = filterDetails.sortBy() == null
                ? SortByStatus.NEWEST
                : filterDetails.sortBy();

        Comparator<ConsultationRequest> comparator = Comparator.comparing(ConsultationRequest::getCreatedAt).reversed();

        if (sortBy == SortByStatus.OLDEST) {
            comparator = Comparator.comparing(ConsultationRequest::getCreatedAt);
        }

        return requestDetails.stream()
                .filter(req -> filterDetails.requestType() == null || filterDetails.requestType().equals(req.getRequestType()))
                .sorted(comparator)
                .map(this::convertRequestToBasicDetails)
                .toList();
    }


    @Transactional
    public ConsultationRequestResponse cancelRequest(Long id, Long requestId) {

        ConsultationRequest request = requestRepository.findById(requestId)
                .orElseThrow(ConsultationRequestNotFoundException::new);

        if (!request.getPatientAccountId().equals(id)) {
            throw new UnauthorizedConsultationRequestAccessException();        }

        if (!request.getStatus().equals(ConsultationRequestStatus.OPEN)) {
            throw new InvalidConsultationRequestStateException("Only open requests can be cancelled");
        }

        request.setStatus(ConsultationRequestStatus.CANCELLED);
        request.setCancelledAt(Instant.now());

        List<ConsultationOffer> pendingOffers =
                offerRepository.findByRequestIdAndStatus(requestId, DoctorOfferStatus.PENDING);

        for (ConsultationOffer offer : pendingOffers) {
            offer.setStatus(DoctorOfferStatus.REJECTED);
        }

        return new ConsultationRequestResponse(
                request.getId(),
                request.getPatientEmail(),
                request.getPreferredStartTime(),
                request.getStatus(),
                request.getCreatedAt()
        );
    }


    private ConsultationRequestBasicDetails convertRequestToBasicDetails(ConsultationRequest request) {
        return new ConsultationRequestBasicDetails(
                request.getId(),
                request.getSpecialityId(),
                request.getIssueDescription(),
                request.getRequestType(),
                request.getPreferredStartTime(),
                request.getStatus(),
                request.getExpiresAt(),
                request.getCreatedAt(),
                request.getUpdatedAt()
        );
    }

    private ConsultationRequestDetails convertRequestToDetails(ConsultationRequest request) {
        return new ConsultationRequestDetails(
                request.getId(),
                request.getSpecialityId(),
                request.getIssueDescription(),
                request.getRequestType(),
                request.getPreferredStartTime(),
                request.getStatus(),
                request.getExpiresAt(),
                request.getFulfilledAt(),
                request.getCancelledAt(),
                request.getCreatedAt(),
                request.getUpdatedAt()
        );
    }
}
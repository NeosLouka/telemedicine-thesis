package com.biopix.telemedicine_backend.doctor.internal.listener;


import com.biopix.telemedicine_backend.doctor.internal.enums.DoctorAvailabilityStatus;
import com.biopix.telemedicine_backend.doctor.internal.repo.DoctorRepository;
import com.biopix.telemedicine_backend.doctor.internal.repo.SpecialityRepository;
import com.biopix.telemedicine_backend.shared.contracts.events.DoctorsToNotify;
import com.biopix.telemedicine_backend.shared.contracts.events.RequestCreatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.modulith.events.ApplicationModuleListener;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class RequestCreatedDoctorListener {

    private final SpecialityRepository specialityRepository;
    private final DoctorRepository doctorRepository;
    private final ApplicationEventPublisher eventPublisher;


    @ApplicationModuleListener
    public void onRequestCreated(RequestCreatedEvent event) {

        List<Long> onlineDoctorAccountIds = doctorRepository.findAccountIdsBySpecialityIdAndAvailabilityStatus(
                event.specialityId(),
                DoctorAvailabilityStatus.ONLINE
        );

        if(!onlineDoctorAccountIds.isEmpty()) {
            log.info("No online doctors found for specialityId: {}", event.specialityId());
        }

        eventPublisher.publishEvent(new DoctorsToNotify(
                onlineDoctorAccountIds,
                "Need a " + specialityRepository.findById(event.specialityId()).get().getName() + ".",
                event.requestId(),
                event.requestType()
        ));

    }
}



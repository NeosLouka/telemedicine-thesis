package com.biopix.telemedicine_backend.doctor.internal.listener;

import com.biopix.telemedicine_backend.shared.contracts.events.SuccessfulLogoutEvent;
import com.biopix.telemedicine_backend.doctor.internal.entity.Doctor;
import com.biopix.telemedicine_backend.doctor.internal.enums.DoctorAvailabilityStatus;
import com.biopix.telemedicine_backend.doctor.internal.exceptions.DoctorNotFoundException;
import com.biopix.telemedicine_backend.doctor.internal.repo.DoctorRepository;
import com.biopix.telemedicine_backend.shared.contracts.events.SuccessfulLoginEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.modulith.events.ApplicationModuleListener;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class AvailabilityStatusListener {

    private final DoctorRepository doctorRepository;

    @ApplicationModuleListener
    public void on(SuccessfulLoginEvent event)
    {
        Doctor doctor = doctorRepository.findByAccountId(event.userId()).orElseThrow(DoctorNotFoundException::new);

        doctor.setAvailabilityStatus(DoctorAvailabilityStatus.ONLINE);
        doctorRepository.save(doctor);

    }

    @ApplicationModuleListener
    public void on(SuccessfulLogoutEvent event)
    {
        Doctor doctor = doctorRepository.findByAccountId(event.id()).orElseThrow(DoctorNotFoundException::new);

        doctor.setAvailabilityStatus(DoctorAvailabilityStatus.OFFLINE);
        doctorRepository.save(doctor);

    }

}

package com.biopix.telemedicine_backend.doctor;


import java.util.List;

public interface DoctorServiceApi {

    List<Object> getRegistrations(String result);


    List<Object> getRegistrationDocuments(Long registrationId, String status, String type);
}

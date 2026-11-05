package com.biopix.telemedicine_backend.admin.internal.dto;


public record AdminInsertProfileDetailsRequest(
        String firstName,
        String lastName,
        String phoneNumber,
        String department
){}

package com.biopix.telemedicine_backend.consultation.internal.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ConsultationSearchResult(
        ConsultationResponse single,
        List<ConsultationResponse> list
) {
    public static ConsultationSearchResult ofSingle(ConsultationResponse single) {
        return new ConsultationSearchResult(single, null);
    }

    public static ConsultationSearchResult ofList(List<ConsultationResponse> list) {
        return new ConsultationSearchResult(null, list);
    }
}
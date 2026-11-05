package com.biopix.telemedicine_backend.doctor.internal.repo;

import com.biopix.telemedicine_backend.doctor.internal.entity.Document;
import com.biopix.telemedicine_backend.doctor.internal.enums.DocumentType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DocumentRepository extends JpaRepository<Document, Long> {

    Optional<Document> findByRegistration_IdAndDocType(Long registrationId , DocumentType docType);


    List<Document> findByRegistration_Id(Long registrationId);
}

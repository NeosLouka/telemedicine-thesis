package com.biopix.telemedicine_backend.shared.contracts.events.internal;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.modulith.events.EventPublication;
import org.springframework.modulith.events.FailedEventPublications;
import org.springframework.modulith.events.ResubmissionOptions;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Slf4j
@Component
@RequiredArgsConstructor
public class EventPublicationReliabilityScheduler {

    private final FailedEventPublications failedEventPublications;

    @Scheduled(fixedDelay = 120_000)
    public void resubmitFailedEventPublications() {

        ResubmissionOptions options = ResubmissionOptions.defaults()
                .withMinAge(Duration.ofSeconds(30))
                .withBatchSize(20)
                .withMaxInFlight(5)
                .withFilter(this::logAndAllowResubmission);

        failedEventPublications.resubmit(options);
    }

    private boolean logAndAllowResubmission(EventPublication publication) {

        log.error(
                "Resubmitting FAILED event with Spring Modulith event publication. id={}, eventType={}, attempts={}, publishedAt={}, lastResubmittedAt={}",
                publication.getIdentifier(),
                publication.getEvent().getClass().getName(),
                publication.getCompletionAttempts(),
                publication.getPublicationDate(),
                publication.getLastResubmissionDate()
        );

        return true;
    }
}

package com.biopix.telemedicine_backend.notification.internal.listener;

import com.biopix.telemedicine_backend.notification.internal.entity.UserFcmToken;
import com.biopix.telemedicine_backend.notification.internal.enums.NotificationTitle;
import com.biopix.telemedicine_backend.notification.internal.repo.FCMRepository;
import com.biopix.telemedicine_backend.notification.internal.service.push.FCMService;
import com.biopix.telemedicine_backend.shared.contracts.events.DoctorsToNotify;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.modulith.events.ApplicationModuleListener;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class DoctorsToNotifyListener {

    private final FCMRepository  fcmRepository;
    private final FCMService fcmService;

    @ApplicationModuleListener
    public void on(DoctorsToNotify doctorsToNotify){

        if(doctorsToNotify.onlineDoctorsToNotify().isEmpty()){
            log.info("No doctors to notify");
            return;
        }

        List<UserFcmToken> fcmTokens =  fcmRepository.findByAccountIdIn(doctorsToNotify.onlineDoctorsToNotify());

        if (fcmTokens.isEmpty()) {
            log.info("No FCM tokens registered for the target doctors");
            return;
        }

        boolean success = fcmService.sendMessageToUsers(
                fcmTokens,
                NotificationTitle.NEW_PATIENT_REQUEST.getTitle(),
                doctorsToNotify.description(),
                doctorsToNotify.type()
        );

        if (success) {
            log.info("Successfully sent notification event to doctors");
        } else {
            log.warn("Failed to deliver notification to some or all target doctors");
        }

    }



}

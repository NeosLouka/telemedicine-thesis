package com.biopix.telemedicine_backend.notification.internal.listener;

import com.biopix.telemedicine_backend.notification.internal.service.push.FCMService;
import com.biopix.telemedicine_backend.shared.contracts.events.SuccessfulLogoutEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.modulith.events.ApplicationModuleListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class FcmTokenEventListener {

    private final FCMService fcmService;

    @ApplicationModuleListener
    public void on(SuccessfulLogoutEvent event) {

        if(event.fcmToken() != null && !event.fcmToken().isBlank() ) {
            fcmService.deleteToken(event.id(), event.fcmToken());
        }else{
            fcmService.deleteAllTokens(event.id());
        }




    }

}

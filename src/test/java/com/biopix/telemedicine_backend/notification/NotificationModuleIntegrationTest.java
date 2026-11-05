package com.biopix.telemedicine_backend.notification;

import com.biopix.telemedicine_backend.notification.internal.entity.UserFcmToken;
import com.biopix.telemedicine_backend.notification.internal.repo.FCMRepository;
import com.biopix.telemedicine_backend.shared.contracts.events.DoctorsToNotify;
import com.biopix.telemedicine_backend.support.WithPostgresContainer;
import com.google.firebase.messaging.BatchResponse;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.MulticastMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.modulith.test.ApplicationModuleTest;
import org.springframework.modulith.test.Scenario;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ApplicationModuleTest
@WithPostgresContainer
class NotificationModuleIntegrationTest {

    @Autowired
    private FCMRepository fcmRepository;

    @MockitoBean
    private FirebaseMessaging firebaseMessaging;

    @BeforeEach
    void cleanTokens() {
        fcmRepository.deleteAll();
    }

    @Test
    void whenDoctorsToNotifyEventPublished_thenTokensAreQueriedAndFcmSent(Scenario scenario) throws Exception {
        BatchResponse dummyResponse = mock(BatchResponse.class);
        when(dummyResponse.getSuccessCount()).thenReturn(1);
        when(firebaseMessaging.sendEachForMulticast(any())).thenReturn(dummyResponse);

        String uniqueToken = "token_" + System.currentTimeMillis();
        fcmRepository.saveAndFlush(UserFcmToken.builder()
                .accountId(100L)
                .fcmToken(uniqueToken)
                .deviceType("ANDROID")
                .build());

        DoctorsToNotify event = new DoctorsToNotify(
                List.of(100L),
                "New Request Pending",
                100L,
                "MARKETPLACE_REQUEST"
        );

        scenario.publish(event)
                .andWaitForStateChange(() -> {
                    try {
                        verify(firebaseMessaging).sendEachForMulticast(any(MulticastMessage.class));
                        return true;
                    } catch (AssertionError e) {
                        return false;
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                });
    }
}

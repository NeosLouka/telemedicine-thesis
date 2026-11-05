package com.biopix.telemedicine_backend.notification;

import com.biopix.telemedicine_backend.notification.internal.entity.UserFcmToken;
import com.biopix.telemedicine_backend.notification.internal.enums.NotificationTitle;
import com.biopix.telemedicine_backend.notification.internal.listener.DoctorsToNotifyListener;
import com.biopix.telemedicine_backend.notification.internal.repo.FCMRepository;
import com.biopix.telemedicine_backend.notification.internal.service.push.FCMService;
import com.biopix.telemedicine_backend.shared.contracts.events.DoctorsToNotify;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DoctorsToNotifyListenerTest {

    @Mock
    private FCMRepository fcmRepository;

    @Mock
    private FCMService fcmService;

    @InjectMocks
    private DoctorsToNotifyListener doctorsToNotifyListener;

    @Test
    void on_ShouldDispatchNotifications_WhenDoctorsExistAndServiceSucceeds() {
        // Given
        List<Long> doctorIds = List.of(10L, 11L);
        DoctorsToNotify event = new DoctorsToNotify(
                doctorIds,
                "New Consultation Available",
                100L,
                "REQUEST"
        );

        List<UserFcmToken> mockTokens = List.of(
                UserFcmToken.builder().accountId(10L).fcmToken("token1").build(),
                UserFcmToken.builder().accountId(11L).fcmToken("token2").build()
        );

        when(fcmRepository.findByAccountIdIn(doctorIds)).thenReturn(mockTokens);
        when(fcmService.sendMessageToUsers(
                mockTokens,
                NotificationTitle.NEW_PATIENT_REQUEST.getTitle(),
                "New Consultation Available",
                "REQUEST"
        )).thenReturn(true);

        // When
        doctorsToNotifyListener.on(event);

        // Then
        verify(fcmRepository, times(1)).findByAccountIdIn(doctorIds);
        verify(fcmService, times(1)).sendMessageToUsers(
                mockTokens,
                NotificationTitle.NEW_PATIENT_REQUEST.getTitle(),
                "New Consultation Available",
                "REQUEST"
        );
    }

    @Test
    void on_ShouldLogWarningAndNotThrow_WhenServiceFails() {
        // Given
        List<Long> doctorIds = List.of(10L);
        DoctorsToNotify event = new DoctorsToNotify(
                doctorIds,
                "New Consultation Available",
                100L,
                "REQUEST"
        );

        List<UserFcmToken> mockTokens = List.of(
                UserFcmToken.builder().accountId(10L).fcmToken("token1").build()
        );

        when(fcmRepository.findByAccountIdIn(doctorIds)).thenReturn(mockTokens);
        when(fcmService.sendMessageToUsers(
                mockTokens,
                NotificationTitle.NEW_PATIENT_REQUEST.getTitle(),
                "New Consultation Available",
                "REQUEST"
        )).thenReturn(false);

        // When
        doctorsToNotifyListener.on(event);

        // Then
        verify(fcmRepository, times(1)).findByAccountIdIn(doctorIds);
        verify(fcmService, times(1)).sendMessageToUsers(
                mockTokens,
                NotificationTitle.NEW_PATIENT_REQUEST.getTitle(),
                "New Consultation Available",
                "REQUEST"
        );
    }

    @Test
    void on_ShouldDoNothing_WhenDoctorListIsEmpty() {
        // Given
        DoctorsToNotify event = new DoctorsToNotify(
                List.of(),
                "Description",
                123L,
                "REQUEST"
        );

        // When
        doctorsToNotifyListener.on(event);

        // Then
        verifyNoInteractions(fcmRepository);
        verifyNoInteractions(fcmService);
    }

    @Test
    void on_ShouldDoNothing_WhenNoTokensFoundForDoctors() {
        // Given
        List<Long> doctorIds = List.of(10L, 11L);
        DoctorsToNotify event = new DoctorsToNotify(
                doctorIds,
                "Description",
                123L,
                "REQUEST"
        );

        when(fcmRepository.findByAccountIdIn(doctorIds)).thenReturn(Collections.emptyList());

        // When
        doctorsToNotifyListener.on(event);

        // Then
        verify(fcmRepository, times(1)).findByAccountIdIn(doctorIds);
        verifyNoInteractions(fcmService);
    }
}
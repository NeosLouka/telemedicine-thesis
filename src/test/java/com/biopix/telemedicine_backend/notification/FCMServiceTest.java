package com.biopix.telemedicine_backend.notification;

import com.biopix.telemedicine_backend.notification.internal.entity.UserFcmToken;
import com.biopix.telemedicine_backend.notification.internal.enums.NotificationTitle;
import com.biopix.telemedicine_backend.notification.internal.repo.FCMRepository;
import com.biopix.telemedicine_backend.notification.internal.service.push.FCMService;
import com.google.firebase.messaging.BatchResponse;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.MulticastMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FCMServiceTest {

    @Mock
    private FCMRepository fcmRepository;

    @Mock
    private FirebaseMessaging firebaseMessaging;

    @InjectMocks
    private FCMService fcmService;

    @Mock
    private BatchResponse batchResponse;

    @BeforeEach
    void setUp() {
        lenient().when(batchResponse.getSuccessCount()).thenReturn(1);
        lenient().when(batchResponse.getFailureCount()).thenReturn(0);
    }

    @Test
    void registerOrUpdateToken_ShouldSaveNewToken_WhenTokenDoesNotExist() {
        when(fcmRepository.findByFcmToken("new-fcm-token")).thenReturn(Optional.empty());

        fcmService.registerOrUpdateToken(1L, "new-fcm-token", "ANDROID");

        verify(fcmRepository, times(1)).save(any(UserFcmToken.class));
    }

    @Test
    void registerOrUpdateToken_ShouldUpdateExistingToken_WhenTokenExists() {
        UserFcmToken existingToken = UserFcmToken.builder()
                .accountId(2L)
                .fcmToken("existing-token")
                .deviceType("IOS")
                .build();

        when(fcmRepository.findByFcmToken("existing-token")).thenReturn(Optional.of(existingToken));

        fcmService.registerOrUpdateToken(1L, "existing-token", "ANDROID");

        assertEquals(1L, existingToken.getAccountId());
        assertEquals("ANDROID", existingToken.getDeviceType());
        verify(fcmRepository, never()).save(any());
    }

    @Test
    void sendMessageToUsers_ShouldPartitionTokensAndReturnTrue_WhenAllBatchesSucceed() throws FirebaseMessagingException {
        // Generate 550 dummy tokens
        List<UserFcmToken> tokens = new ArrayList<>();
        for (int i = 0; i < 550; i++) {
            tokens.add(UserFcmToken.builder().accountId((long) i).fcmToken("token_" + i).build());
        }

        when(firebaseMessaging.sendEachForMulticast(any(MulticastMessage.class))).thenReturn(batchResponse);

        boolean result = fcmService.sendMessageToUsers(
                tokens,
                NotificationTitle.NEW_PATIENT_REQUEST.getTitle(),
                "Test Description",
                NotificationTitle.NEW_PATIENT_REQUEST.getDefaultType()
        );

        assertTrue(result);
        // Verify Firebase was invoked TWICE (1st batch = 500, 2nd batch = 50)
        verify(firebaseMessaging, times(2)).sendEachForMulticast(any(MulticastMessage.class));
    }

    @Test
    void sendMessageToUsers_ShouldReturnFalse_WhenFirebaseMessagingExceptionIsThrown() throws FirebaseMessagingException {
        List<UserFcmToken> tokens = List.of(
                UserFcmToken.builder().accountId(1L).fcmToken("token_1").build()
        );

        when(firebaseMessaging.sendEachForMulticast(any(MulticastMessage.class)))
                .thenThrow(mock(FirebaseMessagingException.class));

        boolean result = fcmService.sendMessageToUsers(
                tokens,
                NotificationTitle.NEW_PATIENT_REQUEST.getTitle(),
                "Test Description",
                "REQUEST"
        );

        assertFalse(result);
        verify(firebaseMessaging, times(1)).sendEachForMulticast(any(MulticastMessage.class));
    }

    @Test
    void sendMessageToUsers_ShouldReturnFalse_WhenBatchHasFailures() throws FirebaseMessagingException {
        List<UserFcmToken> tokens = List.of(
                UserFcmToken.builder().accountId(1L).fcmToken("token_1").build()
        );

        BatchResponse failedBatchResponse = mock(BatchResponse.class);
        when(failedBatchResponse.getSuccessCount()).thenReturn(0);
        when(failedBatchResponse.getFailureCount()).thenReturn(1);

        when(firebaseMessaging.sendEachForMulticast(any(MulticastMessage.class))).thenReturn(failedBatchResponse);

        boolean result = fcmService.sendMessageToUsers(
                tokens,
                NotificationTitle.NEW_PATIENT_REQUEST.getTitle(),
                "Test Description",
                "REQUEST"
        );

        assertFalse(result);
    }

    @Test
    void sendMessageToUsers_ShouldThrowException_WhenTokensIsEmpty() {
        List<UserFcmToken> emptyTokens = List.of();

        assertThrows(IllegalArgumentException.class, () ->
                fcmService.sendMessageToUsers(emptyTokens, "Title", "Description", "TYPE")
        );
    }

    @Test
    void sendMessageToUsers_ShouldThrowException_WhenRequiredArgumentsAreBlank() {
        List<UserFcmToken> tokens = List.of(
                UserFcmToken.builder().accountId(1L).fcmToken("token_1").build()
        );

        assertThrows(IllegalArgumentException.class, () ->
                fcmService.sendMessageToUsers(tokens, "   ", "Description", "TYPE")
        );

        assertThrows(IllegalArgumentException.class, () ->
                fcmService.sendMessageToUsers(tokens, "Title", "", "TYPE")
        );

        assertThrows(IllegalArgumentException.class, () ->
                fcmService.sendMessageToUsers(tokens, "Title", "Description", null)
        );
    }
}
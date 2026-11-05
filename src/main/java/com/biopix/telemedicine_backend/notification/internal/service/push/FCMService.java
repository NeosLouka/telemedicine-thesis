package com.biopix.telemedicine_backend.notification.internal.service.push;

import com.biopix.telemedicine_backend.notification.internal.config.pushNotifications.firebase.FirebaseConfig;
import com.biopix.telemedicine_backend.notification.internal.entity.UserFcmToken;
import com.biopix.telemedicine_backend.notification.internal.repo.FCMRepository;
import com.google.common.collect.Lists;
import com.google.firebase.messaging.*;
import io.jsonwebtoken.lang.Assert;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class FCMService {

    private final FCMRepository fcmRepository;
    private final FirebaseMessaging firebaseMessaging;


    @Transactional
    public void registerOrUpdateToken(Long userId, String tokenStr, String deviceTypeStr)
    {
        //Check Arguments
        if(userId == null || tokenStr == null || deviceTypeStr == null)
            throw new NullPointerException("token or device type string is null");

        fcmRepository.findByFcmToken(tokenStr)
                .ifPresentOrElse(
                        userFcmEntity -> {
                            //Token exists: Re-assign it to the current user
                            userFcmEntity.setAccountId(userId);
                            userFcmEntity.setDeviceType(deviceTypeStr);
                        },
                        () -> {
                            UserFcmToken userFcmToken = UserFcmToken.builder()
                                    .accountId(userId)
                                    .fcmToken(tokenStr)
                                    .deviceType(deviceTypeStr)
                                    .build();

                            fcmRepository.save(userFcmToken);
                        }

                );
    }

    @Transactional
    public void deleteToken(Long userId, String tokenStr) {

        if(tokenStr == null || userId == null){
            throw new NullPointerException("token or device type string is null");
        }

        fcmRepository.deleteByAccountIdAndFcmToken(userId, tokenStr);

    }


    @Transactional
    public void deleteAllTokens(Long userId) {
        if(userId == null){
            throw new NullPointerException("userId is null");
        }

        fcmRepository.deleteByAccountId(userId);

    }


    public boolean sendMessageToUsers(List<UserFcmToken> fcmTokens, String title ,String description, String type) {

        if(fcmTokens == null || fcmTokens.isEmpty()){
            throw new IllegalArgumentException("fcmTokens is empty");
        }

        if(description == null ||  description.isBlank() || type == null || type.isBlank() || title == null || title.isBlank()){
            throw new IllegalArgumentException("description or type is empty");
        }

        List<String> tokens = fcmTokens.stream()
                .map(UserFcmToken::getFcmToken)
                .filter(token -> token != null && !token.isBlank())
                .toList();

        if (tokens.isEmpty()) {
            log.warn("No valid FCM tokens found in the provided list.");
            return false;
        }

        List<List<String>> tokenBatches = Lists.partition(tokens, 500);
        boolean allBatchesSucceeded = true;

        for(List<String> batch : tokenBatches){
            MulticastMessage multicastMessage = MulticastMessage.builder()
                    .addAllTokens(batch)
                    .setNotification(Notification.builder()
                            .setTitle(title)
                            .setBody(description)
                            .build())
                    .putData("type", type)
                    .build();

            try {
                BatchResponse response = firebaseMessaging.sendEachForMulticast(multicastMessage);
                log.info("Batch sent! Success: {}, Failures: {}",
                        response.getSuccessCount(), response.getFailureCount());

                if (response.getFailureCount() > 0) {
                    allBatchesSucceeded = false;
                }

            } catch (FirebaseMessagingException e) {
                log.error("Failed to send multicast FCM batch of size {}", batch.size(), e);
                allBatchesSucceeded = false;
            }
        }

        return allBatchesSucceeded;

    }

    public boolean  sendMessageToUser(String fcmToken, String title, String description, String type) {

        if(fcmToken == null || fcmToken.isEmpty()){
            throw new IllegalArgumentException("fcmToken is empty");
        }

        if(description == null || description.isBlank()|| type == null  || type.isBlank() || title == null || title.isBlank()){
            throw new IllegalArgumentException("description or type or title is empty");
        }

        Message msg = Message.builder()
                .setToken(fcmToken)
                .setNotification(Notification.builder()
                        .setTitle(title)
                        .setBody(description)
                        .build()
                )
                .putData("type", type)
                .build();


        try {

            String messageId = firebaseMessaging.send(msg);
            log.info("Successfully sent FCM message: {}", messageId);
            return true;

        } catch (FirebaseMessagingException e) {

            log.error("Failed to send FCM message to token {}: {}", fcmToken, e.getMessage(), e);
            return false;

        }
    }
}

package com.biopix.telemedicine_backend.notification.internal.repo;

import com.biopix.telemedicine_backend.notification.internal.entity.UserFcmToken;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FCMRepository extends JpaRepository<UserFcmToken, Long>{

    Optional<UserFcmToken> findByFcmToken(String tokenStr);

    Optional<UserFcmToken> findByAccountId(Long accountId);

    void deleteByAccountIdAndFcmToken(Long accountId, String fcmToken);

    void deleteByAccountId(Long accountId);

    List<UserFcmToken> findByAccountIdIn(List<Long> accountIds);




}

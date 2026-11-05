package com.biopix.telemedicine_backend.auth.internal.service.OTPcache;



import com.biopix.telemedicine_backend.auth.internal.service.OTPcache.dto.OtpData;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;


@Component
@ConditionalOnProperty(name = "app.cache.type", havingValue = "caffeine", matchIfMissing = true)
public class CaffeineOtpCacheStore implements OtpCacheStore{

    private static final long OTP_EXPIRATION_MINUTES = 15;

    private final Cache<String, OtpData> cache = Caffeine.newBuilder()
            .expireAfterWrite(OTP_EXPIRATION_MINUTES, TimeUnit.MINUTES)
            .build();


    @Override
    public void put(String key, OtpData  otpData) {

        cache.put(key, otpData);
    }

    @Override
    public OtpData get(String key) {

        return cache.getIfPresent(key);
    }

    @Override
    public void delete(String key) {

        cache.invalidate(key);
    }

}

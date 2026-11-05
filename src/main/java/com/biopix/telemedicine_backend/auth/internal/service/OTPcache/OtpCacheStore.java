package com.biopix.telemedicine_backend.auth.internal.service.OTPcache;

import com.biopix.telemedicine_backend.auth.internal.service.OTPcache.dto.OtpData;

public interface OtpCacheStore {

    void put(String key, OtpData otpData);
    OtpData get(String key);
    void delete(String key);
}

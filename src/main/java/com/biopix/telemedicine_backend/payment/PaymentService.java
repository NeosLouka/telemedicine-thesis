package com.biopix.telemedicine_backend.payment;

public interface PaymentService {

    boolean hasPaymentAccount(Long userId, String role);

    String createPaymentAccountSetup(Long userId, String role);

}

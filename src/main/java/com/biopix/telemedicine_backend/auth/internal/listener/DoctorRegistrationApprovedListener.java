package com.biopix.telemedicine_backend.auth.internal.listener;

import com.biopix.telemedicine_backend.auth.internal.entity.AuthAccount;
import com.biopix.telemedicine_backend.auth.internal.enums.AccountStatus;
import com.biopix.telemedicine_backend.auth.internal.repo.AuthAccountRepository;
import com.biopix.telemedicine_backend.shared.contracts.events.DoctorRegistrationApprovedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.modulith.events.ApplicationModuleListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DoctorRegistrationApprovedListener {

    private final AuthAccountRepository authAccountRepository;

    @ApplicationModuleListener
    public void onDoctorRegistrationApproved(DoctorRegistrationApprovedEvent event) {

        AuthAccount account = authAccountRepository.findById(event.accountId())
                .orElseThrow(() -> new IllegalArgumentException("No auth account found with id: " + event.accountId()));

        if(account.getAccountStatus() == AccountStatus.ACTIVE){
            return;
        }

        if(account.getAccountStatus() != AccountStatus.PENDING_ADMIN_REVIEW){
            throw new IllegalArgumentException("Cannot activate account from this state: " + account.getAccountStatus());
        }

        account.setAccountStatus(AccountStatus.ACTIVE);

        authAccountRepository.save(account);
    }
    

}

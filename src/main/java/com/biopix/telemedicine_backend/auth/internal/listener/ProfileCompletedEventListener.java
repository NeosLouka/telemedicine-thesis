package com.biopix.telemedicine_backend.auth.internal.listener;

import com.biopix.telemedicine_backend.auth.internal.entity.AuthAccount;
import com.biopix.telemedicine_backend.auth.internal.enums.AccountStatus;
import com.biopix.telemedicine_backend.auth.internal.repo.AuthAccountRepository;
import com.biopix.telemedicine_backend.shared.contracts.events.ProfileCompletedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.modulith.events.ApplicationModuleListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProfileCompletedEventListener {

    private final AuthAccountRepository authAccountRepository;


    @ApplicationModuleListener
    public void on(ProfileCompletedEvent event) {

        AuthAccount account = authAccountRepository.findById(event.accountId())
                .orElseThrow(() -> new IllegalArgumentException("Auth account not found with id: " + event.accountId()));

        if (!account.getRole().name().equals(event.role())) {
            throw new IllegalStateException("Profile role does not match auth account role");}

        AccountStatus newStatus = resolveStatusAfterProfileCreation(event.role());
        AccountStatus currentStatus = account.getAccountStatus();

        //Idempotency check: the event is already applied
        if(account.getAccountStatus() == newStatus){
            return;
        }
        //Idempotency / safety : Retransmission will not downgrade doctor status
        if (currentStatus != AccountStatus.PENDING_PROFILE_COMPLETION) {
            return;
        }

        account.setAccountStatus(newStatus);

        authAccountRepository.save(account);
    }

    private AccountStatus resolveStatusAfterProfileCreation(String role) {
        return switch (role) {
            case "PATIENT" -> AccountStatus.ACTIVE;
            case "ADMIN" -> AccountStatus.ACTIVE;
            case "DOCTOR" -> AccountStatus.PENDING_ADMIN_REVIEW;
            default -> throw new IllegalArgumentException("Unsupported role: " + role);
        };
    }
}
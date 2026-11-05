package com.biopix.telemedicine_backend.notification.internal.config.pushNotifications.firebase;


import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "gcp.firebase")
public class FirebaseProperties {

    private Resource serviceAccount;

    public Resource getServiceAccount() {
        return serviceAccount;
    }

    public void setServiceAccount(Resource serviceAccount) {
        this.serviceAccount = serviceAccount;
    }



}

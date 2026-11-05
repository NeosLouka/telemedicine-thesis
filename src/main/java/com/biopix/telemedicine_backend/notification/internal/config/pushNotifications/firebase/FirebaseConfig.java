package com.biopix.telemedicine_backend.notification.internal.config.pushNotifications.firebase;


import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.FirebaseMessaging;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.io.InputStream;

@Configuration
@RequiredArgsConstructor
public class FirebaseConfig {

    private final FirebaseProperties firebaseProperties;


    @Bean
    FirebaseMessaging firebaseMessaging(FirebaseApp firebaseApp) {
        return FirebaseMessaging.getInstance(firebaseApp);
    }


    @Bean
    FirebaseApp firebaseApp(GoogleCredentials credentials) {

        if (FirebaseApp.getApps().isEmpty()) {

            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(credentials)
                    .build();

            return FirebaseApp.initializeApp(options);
        }

        return FirebaseApp.getInstance();
    }

    @Bean
    GoogleCredentials googleCredentials() throws IOException {
        if (firebaseProperties.getServiceAccount() != null) {
            try (InputStream is = firebaseProperties.getServiceAccount().getInputStream()) {
                return GoogleCredentials.fromStream(is);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        else {
            // Use standard credentials chain. Useful when running inside GKE
            return GoogleCredentials.getApplicationDefault();
        }
    }

}

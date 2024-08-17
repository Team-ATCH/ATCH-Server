package project.atch.global.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.io.InputStream;

@Slf4j
@Configuration
public class FCMConfig {

    @Value("${fcm.key.path}")
    private String SERVICE_ACCOUNT_JSON;

    @PostConstruct
    public void init() {
        try {
            ClassPathResource resource = new ClassPathResource(SERVICE_ACCOUNT_JSON);
            InputStream serviceAccount = resource.getInputStream();

            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .build();

            FirebaseApp.initializeApp(options);
            log.info("[FCMConfig] 파이어베이스 서버와의 연결에 성공했습니다.");
        } catch (IOException e) {
            log.error("[FCMConfig] 파이어베이스 서버와의 연결에 실패했습니다.");
        }
    }

}

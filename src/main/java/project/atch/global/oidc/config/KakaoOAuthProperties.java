package project.atch.global.oidc.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties("oauth.kakao")
public class KakaoOAuthProperties {
    private String baseUrl;
    private String clientId;
    private String clientSecret;
    private String redirectUrl;
    private String appId;
    private String adminKey;
    private String webClientId;
    private String webAppId;
}

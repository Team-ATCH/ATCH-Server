package project.atch.global.oidc.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import project.atch.domain.user.OAuthProvider;
import project.atch.global.exception.CustomException;
import project.atch.global.exception.ErrorCode;

@Getter
@Setter
@ConfigurationProperties(prefix = "oauth")
public class OauthProperties {

    private OAuthSecret kakao;
    private OAuthSecret apple;

    @Getter
    @Setter
    public static class OAuthSecret {
        private String baseUrl;
        private String appKey;
        private String clientId;
        private String clientSecret;
        private String redirectUrl;
    }

    public String getBaseUrl(OAuthProvider provider) {
        switch (provider) {
            case KAKAO:
                return getOAuthSecret(kakao).getBaseUrl();
            case APPLE:
                return getOAuthSecret(apple).getBaseUrl();
            default:
                throw new CustomException(ErrorCode.OAUTH_PROVIDER_NOT_FOUND);
        }
    }

    public String getAppKey(OAuthProvider provider) {
        switch (provider) {
            case KAKAO:
                return getOAuthSecret(kakao).getAppKey();
            case APPLE:
                return getOAuthSecret(apple).getAppKey();
            default:
                throw new CustomException(ErrorCode.OAUTH_PROVIDER_NOT_FOUND);
        }
    }

    public String getClientId(OAuthProvider provider) {
        switch (provider) {
            case KAKAO:
                return getOAuthSecret(kakao).getClientId();
            case APPLE:
                return getOAuthSecret(apple).getClientId();
            default:
                throw new CustomException(ErrorCode.OAUTH_PROVIDER_NOT_FOUND);
        }
    }

    public String getClientSecret(OAuthProvider provider) {
        switch (provider) {
            case KAKAO:
                return getOAuthSecret(kakao).getClientSecret();
            case APPLE:
                return getOAuthSecret(apple).getClientSecret();
            default:
                throw new CustomException(ErrorCode.OAUTH_PROVIDER_NOT_FOUND);
        }
    }

    public String getRedirectUrl(OAuthProvider provider) {
        switch (provider) {
            case KAKAO:
                return getOAuthSecret(kakao).getRedirectUrl();
            case APPLE:
                return getOAuthSecret(apple).getRedirectUrl();
            default:
                throw new CustomException(ErrorCode.OAUTH_PROVIDER_NOT_FOUND);
        }
    }

    private OAuthSecret getOAuthSecret(OAuthSecret secret) {
        if (secret == null || secret.getBaseUrl() == null || secret.getAppKey() == null) {
            throw new CustomException(ErrorCode.OAUTH_PROVIDER_NOT_FOUND);
        }
        return secret;
    }
}

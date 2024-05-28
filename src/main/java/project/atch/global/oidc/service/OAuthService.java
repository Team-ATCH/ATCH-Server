package project.atch.global.oidc.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.atch.domain.user.OAuthProvider;
import project.atch.domain.user.User;
import project.atch.domain.user.UserRepository;
import project.atch.global.exception.CustomException;
import project.atch.global.exception.ErrorCode;
import project.atch.global.jwt.JwtTokenProvider;
import project.atch.global.oidc.config.KakaoOAuthProperties;
import project.atch.global.oidc.dto.KakaoTokenResponse;
import project.atch.global.oidc.dto.OIDCDecodePayload;
import project.atch.global.oidc.dto.OIDCPublicKeysResponse;
import project.atch.global.oidc.dto.SocialLoginResponse;
import project.atch.global.oidc.feign.KakaoOauthClient;

import java.util.Optional;

@RequiredArgsConstructor
@Transactional
@Service
public class OAuthService {

    private final KakaoOauthClient kakaoOauthClient;
    private final OauthOIDCService oauthOIDCService;
    private final KakaoOAuthProperties kakaoOAuthProperties;
    private final UserRepository userRepository;
    private final JwtTokenProvider tokenProvider;

    public ResponseEntity socialLogin(OAuthProvider provider, String request) {
        KakaoTokenResponse token = getKakaoOauthToken(request);
        OIDCDecodePayload oidcDecodePayload = getOIDCDecodePayload(provider, token.getIdToken());

        Optional<User> existing = userRepository.findByEmail(oidcDecodePayload.getEmail());


        if (existing.isPresent()) {
            User user = existing.get();
            String accessToken = tokenProvider.createAccessToken(user.getEmail(), user.getRole().toString());
            SocialLoginResponse socialLoginResponse = toSocialLoginResponse(accessToken);
            return new ResponseEntity<>(socialLoginResponse, HttpStatus.OK);

        }

        User user = User.fromKakao(oidcDecodePayload.getNickname(), oidcDecodePayload.getEmail());
        userRepository.save(user);
        String accessToken = tokenProvider.createAccessToken(user.getEmail(), user.getRole().toString());
        SocialLoginResponse socialLoginResponse = toSocialLoginResponse(accessToken);
        return new ResponseEntity<>(socialLoginResponse, HttpStatus.CREATED);
    }


    private KakaoTokenResponse getKakaoOauthToken(String code) {
        try {
            return kakaoOauthClient.kakaoAuth(
                    kakaoOAuthProperties.getClientId(),
                    kakaoOAuthProperties.getRedirectUrl(),
                    code,
                    kakaoOAuthProperties.getClientSecret());
        } catch (Exception ex) {
            throw new CustomException(ErrorCode.REQUEST_VALIDATION_EXCEPTION);
        }
    }

    private OIDCDecodePayload getOIDCDecodePayload(OAuthProvider provider, String idToken) {
        OIDCPublicKeysResponse oidcPublicKeysResponse;

        // TODO APPLE과 KAKAO 구분
        oidcPublicKeysResponse = kakaoOauthClient.getKakaoOIDCOpenKeys();

        return oauthOIDCService.getPayloadFromIdToken(
                idToken,
                kakaoOAuthProperties.getBaseUrl(),
                kakaoOAuthProperties.getAppId(),
                oidcPublicKeysResponse);
    }

    private SocialLoginResponse toSocialLoginResponse(String accessToken) {
        return SocialLoginResponse.builder()
                .accessToken(accessToken).build(); // TODO refreshToken 생성
    }




}

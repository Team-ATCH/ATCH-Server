package project.atch.global.oidc.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.atch.domain.notice.service.NoticeService;
import project.atch.domain.user.entity.*;
import project.atch.domain.user.repository.UserRepository;
import project.atch.global.exception.CustomException;
import project.atch.global.exception.ErrorCode;
import project.atch.global.jwt.JwtTokenProvider;
import project.atch.global.oidc.dto.*;
import project.atch.global.oidc.feign.AppleOauthClient;
import project.atch.global.oidc.feign.KakaoOauthClient;

import java.util.Optional;

import static project.atch.domain.user.entity.OAuthProvider.APPLE;
import static project.atch.domain.user.entity.OAuthProvider.KAKAO;

@RequiredArgsConstructor
@Transactional
@Service
public class OAuthService {

    private final KakaoOauthClient kakaoOauthClient;
    private final AppleOauthClient appleOauthClient;
    private final OauthOIDCService oauthOIDCService;
    private final OauthProperties oauthProperties;
    private final JwtTokenProvider tokenProvider;

    private final UserRepository userRepository;

    private final NoticeService noticeService;

    @Transactional
    public ResponseEntity socialLogin(OAuthProvider provider, String request) {
        OIDCDecodePayload oidcDecodePayload;
        if(provider == KAKAO){
            // 카카오는 id token 받아오는 로직 백엔드에서 수행
            KakaoTokenResponse token = getKakaoOauthToken(provider, request);
            oidcDecodePayload = getOIDCDecodePayload(provider, token.getIdToken());
        } else {
            oidcDecodePayload = getOIDCDecodePayload(provider, request);
        }

        Optional<User> existing = userRepository.findByEmail(oidcDecodePayload.getEmail());

        User user;
        if (existing.isPresent()) {
            user = existing.get();
            String accessToken = tokenProvider.createAccessToken(user.getEmail(), user.getRole().toString());
            SocialLoginResponse socialLoginResponse = toSocialLoginResponse(accessToken);
            return new ResponseEntity<>(socialLoginResponse, HttpStatus.OK);
        } else {
            user = register(provider, oidcDecodePayload);
        }

        String accessToken = tokenProvider.createAccessToken(user.getEmail(), user.getRole().toString());
        SocialLoginResponse socialLoginResponse = toSocialLoginResponse(accessToken);
        grantItemsForWelcome(user);

        return new ResponseEntity<>(socialLoginResponse, HttpStatus.CREATED);
    }

    private User register(OAuthProvider provider, OIDCDecodePayload oidcDecodePayload) {
        User user = User.builder()
                .oAuthProvider(provider)
                .email(oidcDecodePayload.getEmail())
                .nickname(oidcDecodePayload.getNickname())
                .build();
        return userRepository.save(user);
    }

    private void grantItemsForWelcome(User user) {
        if (user.getId() % 2 != 0) {
            noticeService.createItemNotice(user, ItemName.LOVELY);
        } else {
            noticeService.createItemNotice(user, ItemName.ONLINE);
        }

        noticeService.createItemNotice(user, ItemName.PARTY_POPPERS);
    }

    // 인가코드로 카카오 id token 받아오기
    private KakaoTokenResponse getKakaoOauthToken(OAuthProvider provider, String code) {
        try {
            return kakaoOauthClient.kakaoAuth(
                    oauthProperties.getClientId(provider),
                    oauthProperties.getRedirectUrl(provider),
                    code,
                    oauthProperties.getClientSecret(provider));
        } catch (Exception ex) {
            throw new CustomException(ErrorCode.REQUEST_VALIDATION_EXCEPTION);
        }
    }

    // id token 복호화하기
    private OIDCDecodePayload getOIDCDecodePayload(OAuthProvider provider, String idToken) {
        OIDCPublicKeysResponse oidcPublicKeysResponse;

        if (provider.equals(KAKAO)) {
            oidcPublicKeysResponse = kakaoOauthClient.getKakaoOIDCOpenKeys();
        } else if (provider.equals(APPLE)) {
            oidcPublicKeysResponse = appleOauthClient.getAppleOIDCOpenKeys();
        } else {
            throw new CustomException(ErrorCode.OAUTH_PROVIDER_NOT_FOUND);
        }

        return oauthOIDCService.getPayloadFromIdToken(
                idToken,
                oauthProperties.getBaseUrl(provider),
                oauthProperties.getAppKey(provider),
                oidcPublicKeysResponse);
    }

    private SocialLoginResponse toSocialLoginResponse(String accessToken) {
        return SocialLoginResponse.builder()
                .accessToken(accessToken).build(); // TODO refreshToken 생성
    }




}

package project.atch.global.oidc.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.atch.domain.notice.entity.Notice;
import project.atch.domain.notice.service.NoticeService;
import project.atch.domain.user.entity.*;
import project.atch.domain.user.repository.ItemRepository;
import project.atch.domain.notice.repository.NoticeRepository;
import project.atch.domain.user.repository.UserItemRepository;
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

    public ResponseEntity socialLogin(OAuthProvider provider, String request) {
        OIDCDecodePayload oidcDecodePayload = getOIDCDecodePayload(provider, request);

        Optional<User> existing = userRepository.findByEmailAndOAuthProvider(oidcDecodePayload.getEmail(), provider);

        if (existing.isPresent()) {
            User user = existing.get();
            String accessToken = tokenProvider.createAccessToken(user.getEmail(), user.getRole().toString(), provider);
            SocialLoginResponse socialLoginResponse = toSocialLoginResponse(accessToken, false);
            return new ResponseEntity<>(socialLoginResponse, HttpStatus.OK);
        }

        String nickname = oidcDecodePayload.getNickname();
        if (nickname == null){
            nickname = oidcDecodePayload.getEmail();
        }
        User user = User.builder()
                .email(oidcDecodePayload.getEmail())
                .nickname(nickname)
                .oAuthProvider(provider).build();
        userRepository.save(user);

        String accessToken = tokenProvider.createAccessToken(user.getEmail(), user.getRole().toString(), provider);
        SocialLoginResponse socialLoginResponse = toSocialLoginResponse(accessToken, true);

        grantItemsForWelcome(user);
        return new ResponseEntity<>(socialLoginResponse, HttpStatus.OK);
    }

    private void grantItemsForWelcome(User user) {
        if (user.getId() % 2 != 0) {
            noticeService.createItemNotice(user, ItemName.LOVELY);
        } else {
            noticeService.createItemNotice(user, ItemName.ONLINE);
        }

        noticeService.createItemNotice(user, ItemName.PARTY_POPPERS);
    }

    // 백엔드 테스트용(인가 코드로 id token 얻기)
    public String getKakaoOauthToken(String code) {
        try {
            OAuthProvider provider = KAKAO;
            KakaoTokenResponse kakaoTokenResponse = kakaoOauthClient.kakaoAuth(
                    oauthProperties.getClientId(provider),
                    oauthProperties.getRedirectUrl(provider),
                    code,
                    oauthProperties.getClientSecret(provider));

            return kakaoTokenResponse.getIdToken();
        } catch (Exception ex) {
            throw new CustomException(ErrorCode.REQUEST_VALIDATION_EXCEPTION);
        }
    }

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

    private SocialLoginResponse toSocialLoginResponse(String accessToken, boolean newUser) {
        return SocialLoginResponse.builder()
                .accessToken(accessToken)
                .newUser(newUser).build();
    }

}

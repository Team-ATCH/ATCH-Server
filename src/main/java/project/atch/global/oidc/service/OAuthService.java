package project.atch.global.oidc.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.atch.domain.notice.entity.Notice;
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
    private final NoticeRepository noticeRepository;
    private final ItemRepository itemRepository;
    private final UserItemRepository userItemRepository;

    public ResponseEntity socialLogin(OAuthProvider provider, String request) {
        KakaoTokenResponse token = getKakaoOauthToken(provider, request); // TODO 애플의 IdToken 받는 로직 추후 구현
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

        grantItemsForWelcome(user);
        return new ResponseEntity<>(socialLoginResponse, HttpStatus.CREATED);
    }

    private void grantItemsForWelcome(User user) {
        if (user.getId() % 2 != 0) {
            createAndSaveNotice(user, ItemName.LOVELY);
            grantItem(user, ItemName.LOVELY);
        } else {
            createAndSaveNotice(user, ItemName.ONLINE);
            grantItem(user, ItemName.ONLINE);
        }

        createAndSaveNotice(user, ItemName.PARTY_POPPERS);
        grantItem(user, ItemName.PARTY_POPPERS);
    }

    private void createAndSaveNotice(User user, ItemName itemName) {
        Notice notice = Notice.of(itemName, user);
        noticeRepository.save(notice);
    }

    private void grantItem(User user, ItemName itemName) {
        Item item = itemRepository.findById(itemName.getValue()).orElseThrow();
        UserItem userItem = new UserItem(user, item);
        userItemRepository.save(userItem);
    }


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

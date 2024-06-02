package project.atch.global.oidc.feign;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import project.atch.global.oidc.dto.KakaoTokenResponse;
import project.atch.global.oidc.dto.OIDCPublicKeysResponse;

@FeignClient(
        name = "KakaoAuthClient",
        url = "https://kauth.kakao.com")
@Component
public interface KakaoOauthClient {

    @PostMapping(
            "/oauth/token?grant_type=authorization_code&client_id={CLIENT_ID}&redirect_uri={REDIRECT_URI}&code={CODE}&client_secret={CLIENT_SECRET}")
    KakaoTokenResponse kakaoAuth(
            @PathVariable("CLIENT_ID") String clientId,
            @PathVariable("REDIRECT_URI") String redirectUri,
            @PathVariable("CODE") String code,
            @PathVariable("CLIENT_SECRET") String clientSecret);

    @Cacheable(cacheNames = "KakaoOICD", cacheManager = "oidcCacheManager")
    @GetMapping("/.well-known/jwks.json")
    OIDCPublicKeysResponse getKakaoOIDCOpenKeys();

}
package project.atch.global.oidc.feign;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import project.atch.global.oidc.dto.OIDCPublicKeysResponse;

@FeignClient(name = "appleOauthClient", url = "https://appleid.apple.com")
@Component
public interface AppleOauthClient {

    @Cacheable(cacheNames = "AppleOICD", cacheManager = "oidcCacheManager")
    @GetMapping("/auth/keys")
    OIDCPublicKeysResponse getAppleOIDCOpenKeys();

}
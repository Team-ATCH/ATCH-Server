package project.atch.global.oidc.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import project.atch.domain.user.entity.OAuthProvider;
import project.atch.global.oidc.dto.OAuthCodeReqeust;
import project.atch.global.oidc.service.OAuthService;

@RestController
@RequiredArgsConstructor
@Tag(name = "로그인 API")
public class OAuthController {

    private final OAuthService oAuthService;

    @PostMapping("/login")
    public ResponseEntity oauthUserCodeLogin(
            @RequestParam(name = "provider") OAuthProvider provider,
            @RequestBody OAuthCodeReqeust request) {

        return oAuthService.socialLogin(provider, request.getCode());
    }

    @PostMapping("/idToken")
    public String getKakaoIdToken(@RequestBody OAuthCodeReqeust request){
        return oAuthService.getKakaoOauthToken(request.getCode());
    }
}

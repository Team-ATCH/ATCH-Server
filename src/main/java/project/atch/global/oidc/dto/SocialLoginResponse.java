package project.atch.global.oidc.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@Schema(description = "로그인 응답 DTO")
public class SocialLoginResponse {

    private String accessToken;
    private String refreshToken;

}
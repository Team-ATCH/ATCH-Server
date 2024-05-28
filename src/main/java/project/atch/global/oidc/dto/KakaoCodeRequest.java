package project.atch.global.oidc.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "인가 코드")
public class KakaoCodeRequest {
    private String code;
}


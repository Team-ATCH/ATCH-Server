package project.atch.global.oidc.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class OIDCPublicKeysResponse {
    private List<OIDCPublicKeyDto> keys;
}

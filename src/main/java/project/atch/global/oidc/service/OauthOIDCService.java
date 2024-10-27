package project.atch.global.oidc.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import project.atch.global.oidc.dto.OIDCDecodePayload;
import project.atch.global.oidc.dto.OIDCPublicKeyDto;
import project.atch.global.oidc.dto.OIDCPublicKeysResponse;

@RequiredArgsConstructor
@Service
public class OauthOIDCService {

    private final JwtOIDCService jwtOIDCService;

    private String getKidFromUnsignedIdToken(String token, String iss, String aud) {
        return jwtOIDCService.getKidFromUnsignedTokenHeader(token, iss, aud);
    }

    public OIDCDecodePayload getPayloadFromIdToken(
            String token, String iss, String aud, OIDCPublicKeysResponse oidcPublicKeysResponse) {
        String kid = getKidFromUnsignedIdToken(token, iss, aud); // iss: base url, aud: app key

        OIDCPublicKeyDto oidcPublicKeyDto =
                oidcPublicKeysResponse.getKeys().stream()
                        .filter(o -> o.getKid().equals(kid))
                        .findFirst()
                        .orElseThrow();

        return jwtOIDCService.getOIDCTokenBody(
                token, oidcPublicKeyDto.getN(), oidcPublicKeyDto.getE());
    }

}

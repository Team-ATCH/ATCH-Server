package project.atch.global.oidc.service;

import io.jsonwebtoken.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import project.atch.global.exception.CustomException;
import project.atch.global.exception.ErrorCode;
import project.atch.global.oidc.dto.OIDCDecodePayload;

import java.math.BigInteger;
import java.security.Key;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPublicKeySpec;
import java.util.Base64;

@Component
@Slf4j
public class JwtOIDCService {

    private final String KID = "kid";
    public String getKidFromUnsignedTokenHeader(String token, String iss, String aud) {
        return (String) getUnsignedTokenClaims(token, iss, aud).getHeader().get(KID);
    }

    private Jwt<Header, Claims> getUnsignedTokenClaims(String token, String iss, String aud) {
        try {
            return Jwts.parserBuilder()
                    .requireAudience(aud) //aud(app key)가 같은지 확인
                    .requireIssuer(iss) //iss(base url)가 같은지 확인
                    .build()
                    .parseClaimsJwt(getUnsignedToken(token));
        } catch (ExpiredJwtException e) {
            throw new CustomException(ErrorCode.REQUEST_VALIDATION_EXCEPTION); // TODO
        } catch (Exception e) {
            log.error(e.toString());
            throw new CustomException(ErrorCode.REQUEST_VALIDATION_EXCEPTION); // TODO throw InvalidTokenException.EXCEPTION;
        }

    }

    private String getUnsignedToken(String token) {
        String[] splitToken = token.split("\\.");

        if (splitToken.length != 3) {
            throw new CustomException(ErrorCode.REQUEST_VALIDATION_EXCEPTION); // TODO
        }

        return splitToken[0] + "." + splitToken[1] + ".";
    }

    // 공개키로 토큰 바디를 디코드한다.
    public OIDCDecodePayload getOIDCTokenBody(String token, String modulus, String exponent) {
        Claims body = getOIDCTokenJws(token, modulus, exponent).getBody();
        return new OIDCDecodePayload(
                body.getIssuer(),
                body.getAudience(),
                body.getSubject(),
                body.get("nickname", String.class),
                body.get("email", String.class));
    }

    // 공개키로 토큰 검증을 시도한다.
    public Jws<Claims> getOIDCTokenJws(String token, String modulus, String exponent) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(getRSAPublicKey(modulus, exponent))
                    .build()
                    .parseClaimsJws(token);
        } catch (ExpiredJwtException e) {
            throw new CustomException(ErrorCode.REQUEST_VALIDATION_EXCEPTION);
        } catch (Exception e) {
            log.error(e.toString());
            throw new CustomException(ErrorCode.REQUEST_VALIDATION_EXCEPTION);
        }
    }

    private Key getRSAPublicKey(String modulus, String exponent)
            throws NoSuchAlgorithmException, InvalidKeySpecException {
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        byte[] decodeN = Base64.getUrlDecoder().decode(modulus);
        byte[] decodeE = Base64.getUrlDecoder().decode(exponent);
        BigInteger n = new BigInteger(1, decodeN);
        BigInteger e = new BigInteger(1, decodeE);

        RSAPublicKeySpec keySpec = new RSAPublicKeySpec(n, e);
        return keyFactory.generatePublic(keySpec);
    }
}

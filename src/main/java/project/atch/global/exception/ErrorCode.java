package project.atch.global.exception;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum ErrorCode {

    /* 400 BAD REQUEST */
    REQUEST_VALIDATION_EXCEPTION(HttpStatus.BAD_REQUEST, "잘못된 요청입니다."),
    USER_NOT_IN_CHAT_ROOM(HttpStatus.BAD_REQUEST,"잘못된 방에 요청하였습니다."),
    USER_INFORMATION_NOT_FOUND(HttpStatus.BAD_REQUEST,"상대방의 정보를 찾을 수 없습니다."),
    OAUTH_PROVIDER_NOT_FOUND(HttpStatus.BAD_REQUEST,"잘못된 OAuthProvider입니다."),
    ROOM_NOT_FOUND(HttpStatus.BAD_REQUEST, "잘못된 방을 요청하였습니다."),

    /* 401 Unauthorized */
    USER_NOT_FOUND_IN_SESSION(HttpStatus.UNAUTHORIZED, "웹소켓 통신 중 세션에서 사용자를 찾을 수 없습니다.");

    private final HttpStatus httpStatus;
    private final String message;

    public int getHttpStatusCode() {
        return httpStatus.value();
    }

}

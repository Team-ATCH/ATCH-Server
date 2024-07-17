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
    INVALID_REQUEST_CONTECT(HttpStatus.BAD_REQUEST, "잘못된 데이터를 포함한 요청입니다."),
    ITEM_NOT_OWNED(HttpStatus.BAD_REQUEST, "보유하고 있지 않은 아이템입니다."),


    /* 401 Unauthorized */
    USER_NOT_FOUND_IN_SESSION(HttpStatus.UNAUTHORIZED, "웹소켓 통신 중 세션에서 사용자를 찾을 수 없습니다."),
    PERMISSION_DENIED(HttpStatus.UNAUTHORIZED, "사용자의 권한이 없습니다."),

    /* 404 NOT FOUND*/
    ROOM_NOT_FOUND(HttpStatus.NOT_FOUND, "잘못된 방을 요청하였습니다."),
    CHARACTER_NOT_FOUND(HttpStatus.NOT_FOUND, "잘못된 캐릭터를 요청하였습니다."),

    /* 500 Internal Server Error */
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 측에 문제가 생겼습니다."),
    QUERY_EXECUTION_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "데이터베이스 쿼리 실행 중 실패하였습니다.");

    private final HttpStatus httpStatus;
    private final String message;

    public int getHttpStatusCode() {
        return httpStatus.value();
    }

}

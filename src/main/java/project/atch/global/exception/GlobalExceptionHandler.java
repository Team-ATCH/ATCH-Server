package project.atch.global.exception;

import io.micrometer.common.lang.Nullable;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.sql.SQLIntegrityConstraintViolationException;
import java.util.List;
import java.util.stream.Collectors;
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    // Spring MVC에서 발생하는 HTTP 요청 관련 예외
    @Override
    protected ResponseEntity<Object> handleExceptionInternal(
            Exception ex,
            @Nullable Object body,
            HttpHeaders headers,
            HttpStatusCode statusCode,
            WebRequest request) {
        final HttpStatus status = (HttpStatus) statusCode;
        ErrorResponse errorResponse = ErrorResponse.builder()
                .code(status.name())
                .message(ex.getMessage())
                .build();
        return super.handleExceptionInternal(ex, errorResponse, headers, status, request);
    }

    // 메서드 매개변수 유효성 검사 실패 시 발생하는 예외
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {

        List<ErrorResponse.ValidationError> validationErrors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(ErrorResponse.ValidationError::of)
                .collect(Collectors.toList());

        ErrorResponse errorResponse = ErrorResponse.builder()
                .code(ErrorCode.INVALID_REQUEST_CONTENT.name())
                .message(ErrorCode.INVALID_REQUEST_CONTENT.getMessage())
                .errors(validationErrors)
                .build();

        return new ResponseEntity<>(errorResponse, ErrorCode.INVALID_REQUEST_CONTENT.getHttpStatus());
    }

    // 유니크한 컬럼에 중복된 값 삽입 시 발생하는 예외
    @ExceptionHandler({ SQLIntegrityConstraintViolationException.class, DataIntegrityViolationException.class })
    public ResponseEntity<ErrorResponse> handleSQLIntegrityConstraintViolationException(Exception ex) {
        log.error("중복된 값으로 인해 예외가 발생했습니다: {}", ex.getMessage(), ex);

        ErrorResponse errorResponse = ErrorResponse.builder()
                .code(ErrorCode.DUPLICATE_ENTRY.name())
                .message(ErrorCode.DUPLICATE_ENTRY.getMessage())
                .build();

        return new ResponseEntity<>(errorResponse, ErrorCode.DUPLICATE_ENTRY.getHttpStatus());
    }

    // CustomeException 예외
    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ErrorResponse> handleCustomException(CustomException e) {
        log.error(e.getErrorCode().getMessage(), e);
        ErrorCode errorCode = e.getErrorCode();
        return makeErrorResponse(errorCode);
    }

    // 그 외 예외
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
        log.error(ex.getMessage(), ex);
        return makeErrorResponse(ErrorCode.INTERNAL_SERVER_ERROR);
    }

    private ResponseEntity<ErrorResponse> makeErrorResponse(ErrorCode errorCode) {
        ErrorResponse res = ErrorResponse.builder()
                .code(errorCode.name())
                .message(errorCode.getMessage())
                .build();

        return ResponseEntity.status(errorCode.getHttpStatus()).body(res);
    }
}

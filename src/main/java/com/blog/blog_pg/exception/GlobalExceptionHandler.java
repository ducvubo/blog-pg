package com.blog.blog_pg.exception;

import com.blog.blog_pg.dto.response.ApiResponse;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestControllerAdvice;

//@ControllerAdvice
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @Autowired
    private com.blog.blog_pg.utils.LoggingFilter loggingFilter;

    @ExceptionHandler(value = Exception.class)
    ResponseEntity<ApiResponse<Object>> handlingException(RuntimeException exception, HttpServletRequest request, HttpServletResponse response) {
        log.error("Exception: ", exception);
        Long startTime = loggingFilter.getStartTime() != null ? loggingFilter.getStartTime() : System.currentTimeMillis();
        String idUserGuest = request.getHeader("id_user_guest") != null ? request.getHeader("id_user_guest") : "Guest-" + java.util.UUID.randomUUID().toString();
        loggingFilter.logError(request, response, idUserGuest, System.currentTimeMillis() - startTime, exception);

        ApiResponse apiResponse = new ApiResponse();

        apiResponse.setCode(ErrorCode.UNCATEGORIZED_EXCEPTION.getCode());
        apiResponse.setMessage(ErrorCode.UNCATEGORIZED_EXCEPTION.getMessage());

        return ResponseEntity.badRequest().body(apiResponse);
    }

    @ExceptionHandler(value = AppException.class)
    ResponseEntity<ApiResponse<Object>> handlingAppException(AppException exception, HttpServletRequest request, HttpServletResponse response) {
        log.error("Exception: ", exception);
        Long startTime = loggingFilter.getStartTime() != null ? loggingFilter.getStartTime() : System.currentTimeMillis();
        String idUserGuest = request.getHeader("id_user_guest") != null ? request.getHeader("id_user_guest") : "Guest-" + java.util.UUID.randomUUID().toString();
        loggingFilter.logError(request, response, idUserGuest, System.currentTimeMillis() - startTime, exception);
        ErrorCode errorCode = exception.getErrorCode();
        ApiResponse<Object> apiResponse = new ApiResponse<Object>();

        apiResponse.setCode(errorCode.getCode());
        apiResponse.setMessage(errorCode.getMessage());

        return ResponseEntity.status(errorCode.getStatusCode()).body(apiResponse);
    }

    @ExceptionHandler(value = BadRequestError.class)
    ResponseEntity<ApiResponse<Object>> handlingBadRequestException(BadRequestError exception, HttpServletRequest request, HttpServletResponse response) {
        log.error("Exception: ", exception);
        Long startTime = loggingFilter.getStartTime() != null ? loggingFilter.getStartTime() : System.currentTimeMillis();
        String idUserGuest = request.getHeader("id_user_guest") != null ? request.getHeader("id_user_guest") : "Guest-" + java.util.UUID.randomUUID().toString();
        loggingFilter.logError(request, response, idUserGuest, System.currentTimeMillis() - startTime, exception);
        ErrorCode errorCode = exception.getErrorCode();
        ApiResponse<Object> apiResponse = new ApiResponse<Object>();

        apiResponse.setStatusCode(errorCode.getCode());
        apiResponse.setMessage(exception.getMessage());

        return ResponseEntity.status(errorCode.getStatusCode()).body(apiResponse);
    }

    @ExceptionHandler(RuntimeException.class)
    ResponseEntity<ApiResponse<Object>> handlingRuntimeException(RuntimeException ex, HttpServletRequest request, HttpServletResponse response) {
        Long startTime = loggingFilter.getStartTime() != null ? loggingFilter.getStartTime() : System.currentTimeMillis();
        String idUserGuest = request.getHeader("id_user_guest") != null ? request.getHeader("id_user_guest") : "Guest-" + java.util.UUID.randomUUID().toString();
        loggingFilter.logError(request, response, idUserGuest, System.currentTimeMillis() - startTime, ex);
        ApiResponse<Object> apiResponse = new ApiResponse<>();
        apiResponse.setStatusCode(500);
        apiResponse.setMessage(ex.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(apiResponse);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Object>> handleValidationExceptions(MethodArgumentNotValidException ex, HttpServletRequest request, HttpServletResponse response) {
        Long startTime = loggingFilter.getStartTime() != null ? loggingFilter.getStartTime() : System.currentTimeMillis();
        String idUserGuest = request.getHeader("id_user_guest") != null ? request.getHeader("id_user_guest") : "Guest-" + java.util.UUID.randomUUID().toString();
        loggingFilter.logError(request, response, idUserGuest, System.currentTimeMillis() - startTime, ex);
        return ResponseEntity.badRequest().body(ApiResponse.builder()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                //lấy thành 1 list message
                .message(ex.getBindingResult().getFieldErrors().stream().map(error -> error.getDefaultMessage()).toList())
                .build());
    }

    @ExceptionHandler(value = AccessDeniedException.class)
    ResponseEntity<ApiResponse<Object>> handlingAccessDeniedException(AccessDeniedException exception, HttpServletRequest request, HttpServletResponse response) {
        log.error("Exception: ", exception);
        Long startTime = loggingFilter.getStartTime() != null ? loggingFilter.getStartTime() : System.currentTimeMillis();
        String idUserGuest = request.getHeader("id_user_guest") != null ? request.getHeader("id_user_guest") : "Guest-" + java.util.UUID.randomUUID().toString();
        loggingFilter.logError(request, response, idUserGuest, System.currentTimeMillis() - startTime, exception);
        ErrorCode errorCode = ErrorCode.UNAUTHORIZED;

        return ResponseEntity.status(errorCode.getStatusCode())
                .body(ApiResponse.builder()
                        .code(errorCode.getCode())
                        .message(errorCode.getMessage())
                        .build());
    }
}

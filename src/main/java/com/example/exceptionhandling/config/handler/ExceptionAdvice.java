package com.example.exceptionhandling.config.handler;

import static com.example.exceptionhandling.config.handler.ErrorCode.BAD_REQUEST;
import static com.example.exceptionhandling.config.handler.ErrorCode.FORBIDDEN;
import static com.example.exceptionhandling.config.handler.ErrorCode.METHOD_NOT_ALLOWED;
import static com.example.exceptionhandling.config.handler.ErrorCode.UNAUTHORIZED;
import static com.example.exceptionhandling.config.handler.ErrorCode.UNKNOWN;

import com.example.exceptionhandling.config.handler.exception.BusinessException;
import com.example.exceptionhandling.config.handler.exception.PaymentException;
import java.nio.file.AccessDeniedException;
import java.util.UUID;
import javax.security.sasl.AuthenticationException;
import javax.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@Slf4j
@RestControllerAdvice
public class ExceptionAdvice {

  /**
   * UUID 를 통해 서버 로그를 쉽게 검색합니다.
   *
   * @param ex
   * @return
   */
  private UUID generateLogId(Exception ex) {
    UUID uuid = UUID.randomUUID();
    log.error("### {}, {}", uuid, ex.getClass().getSimpleName(), ex);
    return uuid;
  }

  /**
   * 지원하지 않은 HTTP method 호출 할 경우 발생합니다.
   *
   * @param ex
   * @return
   */
  @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
  @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
  protected ErrorResponse handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException ex) {
    return ErrorResponse.of(METHOD_NOT_ALLOWED, generateLogId(ex));
  }

  /**
   * ModelAttribute 에 binding error 발생시 BindException 발생한다.
   *
   * @param ex
   * @return
   */
  @ExceptionHandler(BindException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  protected ErrorResponse handleBindException(BindException ex) {
    return ErrorResponse.of(BAD_REQUEST, ex.getBindingResult(), generateLogId(ex));
  }

  /**
   * Authentication 객체가 필요한 권한을 보유하지 않은 경우 발생합니다.
   *
   * @param ex
   * @return
   */
  @ExceptionHandler(AuthenticationException.class)
  @ResponseStatus(HttpStatus.UNAUTHORIZED)
  protected ErrorResponse handleAuthenticationException(AuthenticationException ex) {
    return ErrorResponse.of(UNAUTHORIZED, generateLogId(ex));
  }

  /**
   * 필요한 param 값이 들어오지 않았을 때 발생합니다.
   *
   * @param ex
   * @return
   */
  @ExceptionHandler(MissingServletRequestParameterException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  protected ErrorResponse handleMissingServletRequestParameterException(MissingServletRequestParameterException ex) {
    return ErrorResponse.of(ex, generateLogId(ex));
  }

  /**
   * type 이 일치하지 않아 binding 못할 경우 발생합니다.
   * 주로 @RequestParam enum 으로 binding 못했을 경우 발생합니다.
   *
   * @param ex
   * @return
   */
  @ExceptionHandler(MethodArgumentTypeMismatchException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  protected ErrorResponse handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException ex) {
    return ErrorResponse.of(ex, generateLogId(ex));
  }

  /**
   * javax.validation 을 통과하지 못하면 에러가 발생합니다.
   * 주로 @NotBlank, @NotEmpty, @NotNull 에서 발생합니다.
   *
   * @param ex
   * @return
   */
  @ExceptionHandler(ConstraintViolationException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  protected ErrorResponse handleConstraintViolationException(ConstraintViolationException ex) {
    return ErrorResponse.of(ex, generateLogId(ex));
  }

  @ExceptionHandler(IllegalArgumentException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  protected ErrorResponse handleIllegalArgumentException(IllegalArgumentException ex) {
    return ErrorResponse.of(BAD_REQUEST, generateLogId(ex));
  }

  /**
   * Valid or Validated 으로 binding error 발생시 발생합니다.
   * HttpMessageConverter 에서 등록한 HttpMessageConverter binding 이 실패하는 경우 발생합니다.
   * 주로 @RequestBody, @RequestPart 어노테이션에서 발생합니다.
   *
   * @param ex
   * @return
   */
  @ExceptionHandler(MethodArgumentNotValidException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  protected ErrorResponse handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
    return ErrorResponse.of(BAD_REQUEST, ex.getBindingResult(), generateLogId(ex));
  }

  /**
   * Authentication 객체가 필요한 권한을 보유하지 않은 경우 발생합니다.
   *
   * @param ex
   * @return
   */
  @ExceptionHandler(AccessDeniedException.class)
  @ResponseStatus(HttpStatus.FORBIDDEN)
  protected ErrorResponse handleAccessDeniedException(AccessDeniedException ex) {
    return ErrorResponse.of(FORBIDDEN, generateLogId(ex));
  }


  /**
   * 커스텀 익셉션은 필요에 맞게 수정합니다.
   *
   * @param ex
   * @return
   */
  @ExceptionHandler(PaymentException.class)
  protected ResponseEntity<ErrorResponse> handlePaymentException(PaymentException ex) {
    return new ResponseEntity<>(ErrorResponse.of(ex, generateLogId(ex)), ex.getErrorCode().getStatus());
  }

  @ExceptionHandler(BusinessException.class)
  protected ResponseEntity<ErrorResponse> handleBusinessException(BusinessException ex) {
    return new ResponseEntity<>(ErrorResponse.of(ex, generateLogId(ex)), ex.getErrorCode().getStatus());
  }

  @ExceptionHandler(Exception.class)
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  protected ErrorResponse handleException(Exception ex) {
    return ErrorResponse.of(UNKNOWN, generateLogId(ex));
  }
}

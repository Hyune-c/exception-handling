package com.example.exceptionhandling.config.handler;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import javax.validation.ConstraintViolationException;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor
public class ErrorResponse {

  private String code;
  private String message;
  private LocalDateTime time;
  private List<FieldError> errors;
  private UUID logId;


  public static ErrorResponse of(ErrorCode errorCode, BindingResult bindingResult, UUID logId) {
    return ErrorResponse.builder()
        .code(errorCode.name())
        .message(errorCode.getReason())
        .time(LocalDateTime.now())
        .errors(FieldError.of(bindingResult))
        .logId(logId)
        .build();
  }

  public static ErrorResponse of(ErrorCode errorCode, List<FieldError> errors, UUID logId) {
    return ErrorResponse.builder()
        .code(errorCode.name())
        .message(errorCode.getReason())
        .time(LocalDateTime.now())
        .errors(errors)
        .logId(logId)
        .build();
  }

  public static ErrorResponse of(ErrorCode errorCode, UUID logId) {
    return ErrorResponse.builder()
        .code(errorCode.name())
        .message(errorCode.getReason())
        .time(LocalDateTime.now())
        .errors(new ArrayList<>())
        .logId(logId)
        .build();
  }

  public static ErrorResponse of(MethodArgumentTypeMismatchException ex, UUID logId) {
    String value = (ex.getValue() == null) ? "" : ex.getValue().toString();
    List<FieldError> errors = List.of(FieldError.of(ex.getName(), value, ex.getErrorCode()));
    return ErrorResponse.of(ErrorCode.BAD_REQUEST, errors, logId);
  }

  public static ErrorResponse of(MissingServletRequestParameterException ex, UUID logId) {
    List<FieldError> errors = List.of(FieldError.of(ex.getParameterName(), null, "Not exist"));
    return ErrorResponse.of(ErrorCode.BAD_REQUEST, errors, logId);
  }

  public static ErrorResponse of(ConstraintViolationException ex, UUID logId) {
    List<FieldError> errors = ex.getConstraintViolations().stream()
        .map(violation ->
            FieldError.of(getPropertyName(violation.getPropertyPath().toString()), null, violation.getMessage()))
        .collect(Collectors.toList());
    return ErrorResponse.of(ErrorCode.BAD_REQUEST, errors, logId);
  }

  private static String getPropertyName(String propertyPath) {
    return propertyPath.substring(propertyPath.lastIndexOf('.') + 1); // 전체 속성 경로에서 속성 이름만 가져옵니다.
  }

  @Getter
  @AllArgsConstructor(access = AccessLevel.PROTECTED)
  public static class FieldError {

    private final String field;
    private final String value;
    private final String reason;

    public static FieldError of(String field, String value, String reason) {
      return new FieldError(field, value, reason);
    }

    public static FieldError of(org.springframework.validation.FieldError fieldError) {
      return FieldError.of(
          fieldError.getField(),
          (fieldError.getRejectedValue() == null) ? "" : fieldError.getRejectedValue().toString(),
          fieldError.getDefaultMessage());
    }

    private static List<FieldError> of(BindingResult bindingResult) {
      return bindingResult.getFieldErrors().stream()
          .map(FieldError::of)
          .collect(Collectors.toList());
    }
  }
}

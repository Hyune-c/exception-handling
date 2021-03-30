package com.example.exceptionhandling.config.handler.exception;

import com.example.exceptionhandling.config.handler.ErrorCode;
import lombok.Getter;

@Getter
public class BusinessException extends RuntimeException {

  private final ErrorCode errorCode;

  public BusinessException(ErrorCode errorCode) {
    super(errorCode.getReason());
    this.errorCode = errorCode;
  }
}

package com.example.exceptionhandling.config.handler;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

  BAD_REQUEST("990400", HttpStatus.BAD_REQUEST, "잘못된 입력 값"),
  UNAUTHORIZED("990401", HttpStatus.UNAUTHORIZED, "인증 실패"),
  FORBIDDEN("990403", HttpStatus.FORBIDDEN, "권한 없음"),
  NOT_FOUND("990404", HttpStatus.NOT_FOUND, "찾을 수 없음"),
  METHOD_NOT_ALLOWED("990405", HttpStatus.METHOD_NOT_ALLOWED, "허용되지 않은 메소드"),
  UNKNOWN("990500", HttpStatus.INTERNAL_SERVER_ERROR, "알수 없는 서버 에러");

  private final String code;
  private final HttpStatus status;
  private final String reason;
}

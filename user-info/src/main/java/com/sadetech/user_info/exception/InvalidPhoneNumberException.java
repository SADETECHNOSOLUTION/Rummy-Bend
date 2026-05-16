package com.sadetech.user_info.exception;

public class InvalidPhoneNumberException extends RuntimeException {
  public InvalidPhoneNumberException(String message) {
    super(message);
  }
}

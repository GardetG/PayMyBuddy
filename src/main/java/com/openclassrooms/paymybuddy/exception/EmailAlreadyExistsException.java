package com.openclassrooms.paymybuddy.exception;

/**
 * Exception Class thrown when email address already used.
 */
public class EmailAlreadyExistsException extends Exception {

  public EmailAlreadyExistsException(String s) {
    super(s);
  }

}

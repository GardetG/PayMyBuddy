package com.openclassrooms.paymybuddy.exception;

/**
 * Exception Class thrown when requesting an operation forbidden by business rules.
 */
public class ForbiddenOperationException extends Exception {

  public ForbiddenOperationException(String s) {
    super(s);
  }

}
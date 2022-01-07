package com.openclassrooms.paymybuddy.exception;

/**
 * Exception Class thrown when requesting an operation forbidden by business rules.
 */
public class ForbbidenOperationException extends Exception {

  public ForbbidenOperationException(String s) {
    super(s);
  }

}
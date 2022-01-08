package com.openclassrooms.paymybuddy.exception;

/**
 * Exception Class thrown when requesting money transfer while provision is insufficient.
 */
public class InsufficientProvisionException extends ForbiddenOperationException {

  public InsufficientProvisionException(String s) {
    super(s);
  }
}

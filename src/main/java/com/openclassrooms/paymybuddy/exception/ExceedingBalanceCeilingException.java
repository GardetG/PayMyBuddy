package com.openclassrooms.paymybuddy.exception;

/**
 * Exception Class thrown when requesting money transfer exceeding the balance ceiling.
 */
public class ExceedingBalanceCeilingException extends ForbiddenOperationException {

  public ExceedingBalanceCeilingException(String s) {
    super(s);
  }

}

package com.openclassrooms.paymybuddy.exception;

/**
 * Exception Class thrown when a requested resource is not found.
 */
public class ResourceNotFoundException extends Exception {

  public ResourceNotFoundException(String s) {
    super(s);
  }

}

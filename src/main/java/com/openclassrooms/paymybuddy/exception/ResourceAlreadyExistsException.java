package com.openclassrooms.paymybuddy.exception;

/**
 * Exception Class thrown when trying to add an already existing resource.
 */
public class ResourceAlreadyExistsException extends Exception {

  public ResourceAlreadyExistsException(String s) {
    super(s);
  }

}
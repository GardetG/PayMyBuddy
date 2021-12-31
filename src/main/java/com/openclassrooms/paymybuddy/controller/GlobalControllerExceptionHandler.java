package com.openclassrooms.paymybuddy.controller;

import com.openclassrooms.paymybuddy.exception.ResourceNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * Class handling exceptions thrown by Service in Controller and generate the HTTP response.
 */
@ControllerAdvice
public class GlobalControllerExceptionHandler {

  private static final Logger LOGGER = LoggerFactory
      .getLogger(GlobalControllerExceptionHandler.class);

  /**
   * Handle ResourceNotFoundException thrown when the resource can't be found.
   *

   * @param ex instance of the exception
   * @return HTTP 404 response
   */
  @ExceptionHandler(ResourceNotFoundException.class)
  public ResponseEntity<String> handleNotFoundExceptions(ResourceNotFoundException ex) {
    String error = ex.getMessage();
    LOGGER.info("Response : 404 {}", error);
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
  }

}

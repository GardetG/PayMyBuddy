package com.openclassrooms.paymybuddy.controller;

import com.openclassrooms.paymybuddy.exception.ForbbidenOperationException;
import com.openclassrooms.paymybuddy.exception.ResourceAlreadyExistsException;
import com.openclassrooms.paymybuddy.exception.ResourceNotFoundException;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
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

  /**
   * Handle EmailAlreadyExistsException thrown when the resource already exists.
   *
   * @param ex instance of the exception
   * @return HTTP 409 response
   */
  @ExceptionHandler(ResourceAlreadyExistsException.class)
  public ResponseEntity<String> handleResourceAlreadyExistsException(
      ResourceAlreadyExistsException ex) {
    String error = ex.getMessage();
    LOGGER.info("Response : 409 {}", error);
    return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
  }

  /**
   * Handle ForbbidenOperationException thrown when requesting an operation forbidden by business
   * rules.
   *

   * @param ex instance of the exception
   * @return HTTP 409 response
   */
  @ExceptionHandler(ForbbidenOperationException.class)
  public ResponseEntity<String> handleForbbidenOperationException(ForbbidenOperationException ex) {
    String error = ex.getMessage();
    LOGGER.info("Response : 409 {}", error);
    return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
  }

  /**
   * Handle MethodArgumentNotValidException thrown when validation failed.
   *

   * @param ex instance of the exception
   * @return HTTP 422 response with information on invalid fields
   */
  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<Map<String, String>> handleValidationExceptions(
      MethodArgumentNotValidException ex) {
    Map<String, String> errors = new HashMap<>();
    ex.getBindingResult().getAllErrors().forEach(error -> {
      String fieldName = ((FieldError) error).getField();
      String errorMessage = error.getDefaultMessage();
      errors.put(fieldName, errorMessage);
    });
    LOGGER.info("Response : 422 invalid DTO");
    return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(errors);
  }
}


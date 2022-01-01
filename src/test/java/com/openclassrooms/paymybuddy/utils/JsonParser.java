package com.openclassrooms.paymybuddy.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.stereotype.Component;

/**
 * Utility Class for parsing object in tests.
 */
@Component
public class JsonParser {

  /**
   * Parse an object into Json String.
   *

   * @param object to parse
   * @return Json String
   */
  public static String asString(final Object object) {
    try {
      return new ObjectMapper()
          .registerModule(new JavaTimeModule())
          .writeValueAsString(object);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

}

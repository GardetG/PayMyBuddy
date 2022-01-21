package com.openclassrooms.paymybuddy.integration.utils;

import org.springframework.util.Base64Utils;

public class CredentialUtils {

  public static String encode(String username, String password) {
    String credentials = String.format("%s:%s", username, password);
    return "Basic " + Base64Utils.encodeToString(credentials.getBytes());
  }

}

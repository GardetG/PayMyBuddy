package com.openclassrooms.paymybuddy.utils;

import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import javax.persistence.AttributeConverter;
import org.springframework.stereotype.Component;

/**
 * Attribute converter for encrypting and decrypting attribute in the database using AES.
 */
@Component
public class AttributeEncryptor implements AttributeConverter<String, String> {

  private static final String AES = "AES/GCM/NoPadding";
  private static final String SECRET = "paymybuddy_crypt";

  private final Key key;
  private final Cipher cipher;

  public AttributeEncryptor() throws NoSuchPaddingException, NoSuchAlgorithmException {
    key = new SecretKeySpec(SECRET.getBytes(), AES);
    cipher = Cipher.getInstance(AES);
  }

  @Override
  public String convertToDatabaseColumn(String attribute) {
    try {
      cipher.init(Cipher.ENCRYPT_MODE, key);
      return Base64.getEncoder().encodeToString(cipher.doFinal(attribute.getBytes()));
    } catch (IllegalBlockSizeException | BadPaddingException | InvalidKeyException e) {
      throw new IllegalStateException(e);
    }
  }

  @Override
  public String convertToEntityAttribute(String dbData) {
    try {
      cipher.init(Cipher.DECRYPT_MODE, key);
      return new String(cipher.doFinal(Base64.getDecoder().decode(dbData)));
    } catch (InvalidKeyException | BadPaddingException | IllegalBlockSizeException e) {
      throw new IllegalStateException(e);
    }
  }
}
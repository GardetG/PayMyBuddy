package com.openclassrooms.paymybuddy.utils;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Attribute converter for encrypting and decrypting attribute in the database.
 */
@Component
public class AttributeEncryptor implements AttributeConverter<String, String> {

  private static final String AES = "AES";
  private static final Charset CHARSET = StandardCharsets.UTF_8;

  private final Key key;
  private final Cipher cipher;

  /**
   * AttributeEncryptor constructor to instantiate cipher and generate the key from the
   * keyword defined in application properties.
   *
   * @param keyword used to generate encryption key.
   */
  public AttributeEncryptor(@Value("${PayMyBuddy.crypt.keyword}") String keyword) {
    try {
      cipher = Cipher.getInstance(AES);
      key = new SecretKeySpec(keyword.getBytes(CHARSET), AES);
    } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
      throw new IllegalStateException(e);
    }
  }

  /**
   * Crypt the provided String using the cipher and key generated in the constructor to store the
   * value in the database.
   *
   * @param attribute to crypt
   * @return encrypted attribute
   */
  @Override
  public String convertToDatabaseColumn(String attribute) {
    try {
      cipher.init(Cipher.ENCRYPT_MODE, key);
      return Base64.getEncoder().encodeToString(cipher.doFinal(attribute.getBytes(CHARSET)));
    } catch (IllegalBlockSizeException | BadPaddingException | InvalidKeyException e) {
      throw new IllegalStateException(e);
    }
  }

  /**
   * Decrypt the provided String using the cipher and key generated in the constructor to return the
   * value in the entity.
   *
   * @param dbData to decrypt
   * @return decrypted attribute
   */
  @Override
  public String convertToEntityAttribute(String dbData) {
    try {
      cipher.init(Cipher.DECRYPT_MODE, key);
      return new String(cipher.doFinal(Base64.getDecoder().decode(dbData)), CHARSET);
    } catch (InvalidKeyException | BadPaddingException | IllegalBlockSizeException e) {
      throw new IllegalStateException(e);
    }
  }
}
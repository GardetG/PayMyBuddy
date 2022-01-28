package com.openclassrooms.paymybuddy.utils;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Base64;
import javax.crypto.Cipher;
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
  private final String keyword;

  /**
   * AttributeEncryptor constructor to instantiate cipher and generate the key from the
   * keyword defined in application properties.
   *
   * @param keyword used to generate encryption key.
   */
  public AttributeEncryptor(@Value("${PayMyBuddy.crypt.keyword}") String keyword) {
    this.keyword = keyword;
  }

  /**
   * Crypt the provided String using the cipher and key generated to store the value in the
   * database.
   *
   * @param attribute to crypt
   * @return encrypted attribute
   */
  @Override
  public String convertToDatabaseColumn(String attribute) {
    Key key = new SecretKeySpec(keyword.getBytes(CHARSET), AES);
    try {
      Cipher c = Cipher.getInstance(AES);
      c.init(Cipher.ENCRYPT_MODE, key);
      return Base64.getEncoder().encodeToString(c.doFinal(attribute.getBytes(CHARSET)));
    } catch (Exception e) {
      throw new IllegalStateException(e);
    }
  }

  /**
   * Decrypt the provided String using the cipher and key generated to return the value in the
   * entity.
   *
   * @param dbData to decrypt
   * @return decrypted attribute
   */
  @Override
  public String convertToEntityAttribute(String dbData) {
    Key key = new SecretKeySpec(keyword.getBytes(CHARSET), AES);
    try {
      Cipher c = Cipher.getInstance(AES);
      c.init(Cipher.DECRYPT_MODE, key);
      return new String(c.doFinal(Base64.getDecoder().decode(dbData)), CHARSET);
    } catch (Exception e) {
      throw new IllegalStateException(e);
    }
  }
}
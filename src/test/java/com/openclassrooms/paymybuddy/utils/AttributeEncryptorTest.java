package com.openclassrooms.paymybuddy.utils;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class AttributeEncryptorTest {

  @Autowired
  private AttributeEncryptor attributeEncryptor;

  @Test
  void convertToDatabaseColumnTest() {
    String clearMessage = "Hello World";

    String encryptedMessage = attributeEncryptor.convertToDatabaseColumn(clearMessage);

    assertThat(encryptedMessage).isNotEqualTo(clearMessage);
  }

  @Test
  void convertToEntityAttributeTest() {
    String encryptedMessage = "gvsXfv53fCnMUa5/njUe0Q==";

    String clearMessage = attributeEncryptor.convertToEntityAttribute(encryptedMessage);

    assertThat(clearMessage).isEqualTo("Hello World");
  }


}

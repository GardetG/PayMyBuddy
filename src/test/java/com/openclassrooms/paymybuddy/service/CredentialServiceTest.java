package com.openclassrooms.paymybuddy.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.openclassrooms.paymybuddy.model.Role;
import com.openclassrooms.paymybuddy.model.User;
import com.openclassrooms.paymybuddy.repository.UserRepository;
import java.time.LocalDateTime;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

@SpringBootTest
class CredentialServiceTest {

  @Autowired
  private CredentialsService credentialService;

  @MockBean
  private UserRepository userRepository;

  private User userTest;

  @BeforeEach
  void setUp() {
    userTest =
        new User("test", "test", "test@mail.com", "12345678", Role.USER, LocalDateTime.now());
    userTest.setUserId(1);
  }

  @DisplayName("Load user by username should return a UserDetails user")
  @Test
  void loadUserByUsernameTest() {
    // GIVEN
    when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(userTest));

    // WHEN
    UserDetails actualUser = credentialService.loadUserByUsername("test@mail.com");

    // THEN
    assertThat(actualUser).isEqualTo(userTest);
    verify(userRepository, times(1)).findByEmail("test@mail.com");
  }

  @DisplayName("Load a non existent user by username should throw an exception")
  @Test
  void loadUserByUsernameWhenNotFoundTest() {
    // GIVEN
    when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());

    // WHEN
    assertThatThrownBy(() -> credentialService.loadUserByUsername("NotExist@mail.com"))

        // THEN
        .isInstanceOf(UsernameNotFoundException.class)
        .hasMessageContaining("Can't login, this user not found");
  }
}

package com.openclassrooms.paymybuddy.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.openclassrooms.paymybuddy.dto.UserInfoDto;
import com.openclassrooms.paymybuddy.dto.UserRegistrationDto;
import com.openclassrooms.paymybuddy.exception.EmailAlreadyExistsException;
import com.openclassrooms.paymybuddy.exception.ResourceNotFoundException;
import com.openclassrooms.paymybuddy.model.User;
import com.openclassrooms.paymybuddy.repository.UserRepository;
import java.math.BigDecimal;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootTest
class UserServiceTest {

  @Autowired
  private UserService userService;

  @MockBean
  UserRepository userRepository;

  @MockBean
  PasswordEncoder passwordEncoder;

  @Captor
  ArgumentCaptor<User> userCaptor;

  private User userTest;
  private UserInfoDto userInfoDto;

  @BeforeEach
  void setUp() {
    userTest = new User(1,"test","test","test@mail.com","12345678", BigDecimal.ZERO);
    userInfoDto = new UserInfoDto(1, "test","test","test@mail.com",BigDecimal.ZERO);
  }

  @Test
  void getInfoByIdTest() throws Exception {
    // GIVEN
    when(userRepository.findById(anyInt())).thenReturn(Optional.of(userTest));

    // WHEN
    UserInfoDto actualUserinfoDto = userService.getInfoById(1);

    // THEN
    assertThat(actualUserinfoDto).usingRecursiveComparison().isEqualTo(userInfoDto);
    verify(userRepository, times(1)).findById(1);
  }

  @Test
  void getInfoByIdWhenNotFoundTest() {
    // GIVEN
    when(userRepository.findById(anyInt())).thenReturn(Optional.empty());

    // WHEN
    assertThatThrownBy(() -> userService.getInfoById(2))

        // THEN
        .isInstanceOf(ResourceNotFoundException.class)
        .hasMessageContaining("This user is not found");
    verify(userRepository, times(1)).findById(2);
  }

  @Test
  void registerTest() throws Exception {
    // GIVEN
    UserRegistrationDto
        subscriptionDto = new UserRegistrationDto("test","test", "test@mail.com", "12345678");
    userTest.setUserId(0);
    userInfoDto.setUserId(0);
    when(userRepository.existsByEmail(anyString())).thenReturn(false);
    when(userRepository.save(any(User.class))).thenReturn(userTest);
    when(passwordEncoder.encode(anyString())).thenReturn("12345678");

    // WHEN
    UserInfoDto actualUserinfoDto = userService.register(subscriptionDto);

    // THEN
    assertThat(actualUserinfoDto).usingRecursiveComparison().isEqualTo(userInfoDto);
    verify(userRepository, times(1)).existsByEmail("test@mail.com");
    verify(userRepository, times(1)).save(userCaptor.capture());
    assertThat(userCaptor.getValue()).usingRecursiveComparison().isEqualTo(userTest);
  }

  @Test
  void registerWhenEmailAlreadyExistTest() {
    // GIVEN
    UserRegistrationDto
        subscriptionDto = new UserRegistrationDto("test","test", "existing@mail.com", "12345678");
    when(userRepository.existsByEmail(anyString())).thenReturn(true);

    // WHEN
    assertThatThrownBy(() -> userService.register(subscriptionDto))

        // THEN
        .isInstanceOf(EmailAlreadyExistsException.class)
        .hasMessageContaining("This email is already used");
    verify(userRepository, times(1)).existsByEmail("existing@mail.com");
    verify(userRepository, times(0)).save(any(User.class));
  }

  @Test
  void updateInfoWithSameEmailTest() throws Exception {
    // GIVEN
    UserInfoDto updateDto = new UserInfoDto(1, "update","test", "test@mail.com", BigDecimal.ZERO);
    User updatedUser = new User(1, "update", "test", "test@mail.com", "12345678", BigDecimal.ZERO);
    when(userRepository.findById(anyInt())).thenReturn(Optional.of(userTest));
    when(userRepository.existsByEmail(anyString())).thenReturn(true);
    when(userRepository.save(any(User.class))).thenReturn(updatedUser);

    // WHEN
    UserInfoDto actualUserinfoDto = userService.update(updateDto);

    // THEN
    assertThat(actualUserinfoDto).usingRecursiveComparison().isEqualTo(updateDto);
    verify(userRepository, times(1)).findById(1);
    verify(userRepository, times(0)).existsByEmail(anyString());
    verify(userRepository, times(1)).save(userCaptor.capture());
    assertThat(userCaptor.getValue()).usingRecursiveComparison().isEqualTo(updatedUser);
  }

  @Test
  void updateInfoWithNewEmailTest() throws Exception {
    // GIVEN
    UserInfoDto updateDto = new UserInfoDto(1, "update","test", "update@mail.com", BigDecimal.ZERO);
    User updatedUser = new User(1, "update", "test", "update@mail.com", "12345678", BigDecimal.ZERO);
    when(userRepository.findById(anyInt())).thenReturn(Optional.of(userTest));
    when(userRepository.existsByEmail(anyString())).thenReturn(false);
    when(userRepository.save(any(User.class))).thenReturn(updatedUser);

    // WHEN
    UserInfoDto actualUserinfoDto = userService.update(updateDto);

    // THEN
    assertThat(actualUserinfoDto).usingRecursiveComparison().isEqualTo(updateDto);
    verify(userRepository, times(1)).findById(1);
    verify(userRepository, times(1)).existsByEmail("update@mail.com");
    verify(userRepository, times(1)).save(userCaptor.capture());
    assertThat(userCaptor.getValue()).usingRecursiveComparison().isEqualTo(updatedUser);
  }

  @Test
  void updateInfoWhenNotFoundTest() {
    // GIVEN
    UserInfoDto updateDto = new UserInfoDto(2, "update","test", "update@mail.com", BigDecimal.ZERO);
    when(userRepository.findById(anyInt())).thenReturn(Optional.empty());

    // WHEN
    assertThatThrownBy(() -> userService.update(updateDto))

        // THEN
        .isInstanceOf(ResourceNotFoundException.class)
        .hasMessageContaining("This user is not found");
    verify(userRepository, times(1)).findById(2);
    verify(userRepository, times(0)).existsByEmail(anyString());
    verify(userRepository, times(0)).save(any(User.class));
  }

  @Test
  void updateInfoWhenNewEmailAlreadyExistsTest() {
    // GIVEN
    UserInfoDto updateDto = new UserInfoDto(1, "update","test", "existing@mail.com",BigDecimal.ZERO);
    when(userRepository.findById(anyInt())).thenReturn(Optional.of(userTest));
    when(userRepository.existsByEmail(anyString())).thenReturn(true);

    // WHEN
    assertThatThrownBy(() -> userService.update(updateDto))

        // THEN
        .isInstanceOf(EmailAlreadyExistsException.class)
        .hasMessageContaining("This email is already used");
    verify(userRepository, times(1)).findById(1);
    verify(userRepository, times(1)).existsByEmail("existing@mail.com");
    verify(userRepository, times(0)).save(any(User.class));
  }

  @Test
  void deleteByIdTest() throws Exception {
    // GIVEN
    when(userRepository.existsById(anyInt())).thenReturn(true);

    // WHEN
    userService.deleteById(1);

    // THEN
    verify(userRepository, times(1)).existsById(1);
    verify(userRepository, times(1)).deleteById(1);
  }

  @Test
  void deleteByIdWhenNotFoundTest() {
    // GIVEN
    when(userRepository.existsById(anyInt())).thenReturn(false);

    // WHEN
    assertThatThrownBy(() -> userService.deleteById(2))

        // THEN
        .isInstanceOf(ResourceNotFoundException.class)
        .hasMessageContaining("This user is not found");
    verify(userRepository, times(1)).existsById(2);
    verify(userRepository, times(0)).deleteById(anyInt());
  }
}

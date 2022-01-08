package com.openclassrooms.paymybuddy.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.openclassrooms.paymybuddy.dto.UserDto;
import com.openclassrooms.paymybuddy.exception.ForbiddenOperationException;
import com.openclassrooms.paymybuddy.exception.ResourceAlreadyExistsException;
import com.openclassrooms.paymybuddy.exception.ResourceNotFoundException;
import com.openclassrooms.paymybuddy.model.Role;
import com.openclassrooms.paymybuddy.model.User;
import com.openclassrooms.paymybuddy.repository.UserRepository;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
  private UserDto userInfoDto;

  @BeforeEach
  void setUp() {
    userTest = new User("user","test","user@mail.com","EncodedPwd", Role.USER);
    userTest.setUserId(1);
    userInfoDto = new UserDto(1, "user","test","user@mail.com",null,BigDecimal.ZERO, "USER");
  }

  @Test
  void getAllTest() {
    // GIVEN
    Pageable pageable = PageRequest.of(0,1);
    when(userRepository.findAll(any(Pageable.class))).thenReturn(new PageImpl<>(List.of(userTest)));

    // WHEN
    Page<UserDto> actualPageUserinfoDto = userService.getAll(pageable);

    // THEN
    assertThat(actualPageUserinfoDto.getContent()).usingRecursiveComparison().isEqualTo(List.of(userInfoDto));
    verify(userRepository, times(1)).findAll(pageable);
  }

  @Test
  void getAllWhenEmptyTest() {
    // GIVEN
    Pageable pageable = PageRequest.of(0,1);
    when(userRepository.findAll(any(Pageable.class))).thenReturn(Page.empty());

    // WHEN
    Page<UserDto> actualPageUserinfoDto = userService.getAll(pageable);

    // THEN
    assertThat(actualPageUserinfoDto.getContent()).isEmpty();
    verify(userRepository, times(1)).findAll(pageable);
  }

  @Test
  void getByIdTest() throws Exception {
    // GIVEN
    when(userRepository.findById(anyInt())).thenReturn(Optional.of(userTest));

    // WHEN
    UserDto actualUserinfoDto = userService.getById(1);

    // THEN
    assertThat(actualUserinfoDto).usingRecursiveComparison().isEqualTo(userInfoDto);
    verify(userRepository, times(1)).findById(1);
  }

  @Test
  void getByIdWhenNotFoundTest() {
    // GIVEN
    when(userRepository.findById(anyInt())).thenReturn(Optional.empty());

    // WHEN
    assertThatThrownBy(() -> userService.getById(2))

        // THEN
        .isInstanceOf(ResourceNotFoundException.class)
        .hasMessageContaining("This user is not found");
    verify(userRepository, times(1)).findById(2);
  }

  @Test
  void registerTest() throws Exception {
    // GIVEN
    UserDto userDto = new UserDto(0,"user","test", "user@mail.com", "12345678", null, null);
    when(userRepository.existsByEmail(anyString())).thenReturn(false);
    when(passwordEncoder.encode(anyString())).thenReturn("EncodedPwd");
    when(userRepository.save(any(User.class))).thenReturn(userTest);

    // WHEN
    UserDto actualUserinfoDto = userService.register(userDto);

    // THEN
    assertThat(actualUserinfoDto).usingRecursiveComparison().isEqualTo(userInfoDto);
    verify(userRepository, times(1)).existsByEmail("user@mail.com");
    verify(passwordEncoder, times(1)).encode("12345678");
    verify(userRepository, times(1)).save(userCaptor.capture());
    userTest.setUserId(0);
    assertThat(userCaptor.getValue()).usingRecursiveComparison().isEqualTo(userTest);
  }

  @Test
  void registerWhenEmailAlreadyExistTest() {
    // GIVEN
    UserDto userDto = new UserDto(0,"user","test", "existing@mail.com", "12345678", null, null);
    when(userRepository.existsByEmail(anyString())).thenReturn(true);

    // WHEN
    assertThatThrownBy(() -> userService.register(userDto))

        // THEN
        .isInstanceOf(ResourceAlreadyExistsException.class)
        .hasMessageContaining("This email is already used");
    verify(userRepository, times(1)).existsByEmail("existing@mail.com");
    verify(passwordEncoder, times(0)).encode(anyString());
    verify(userRepository, times(0)).save(any(User.class));
  }

  @Test
  void updateInfoWithSameEmailTest() throws Exception {
    // GIVEN
    UserDto userDto = new UserDto(1, "update","test", "user@mail.com",null, null, null);
    UserDto updateDto = new UserDto(1, "update","test", "user@mail.com",null, BigDecimal.ZERO, Role.USER.toString());
    User updatedUser = new User("update", "test", "user@mail.com", "EncodedPwd", Role.USER);
    updatedUser.setUserId(1);
    when(userRepository.findById(anyInt())).thenReturn(Optional.of(userTest));
    when(userRepository.save(any(User.class))).thenReturn(updatedUser);

    // WHEN
    UserDto actualUserinfoDto = userService.update(userDto);

    // THEN
    assertThat(actualUserinfoDto).usingRecursiveComparison().isEqualTo(updateDto);
    verify(userRepository, times(1)).findById(1);
    verify(userRepository, times(1)).save(userCaptor.capture());
    assertThat(userCaptor.getValue()).usingRecursiveComparison().isEqualTo(updatedUser);
  }

  @Test
  void updateInfoWithNewEmailTest() throws Exception {
    // GIVEN
    UserDto userDto = new UserDto(1, "update","test", "update@mail.com",null, null, null);
    UserDto
        updateDto = new UserDto(1, "update","test", "update@mail.com",null, BigDecimal.ZERO, Role.USER.toString());
    User updatedUser = new User("update", "test", "update@mail.com", "EncodedPwd", Role.USER);
    updatedUser.setUserId(1);
    when(userRepository.findById(anyInt())).thenReturn(Optional.of(userTest));
    when(userRepository.existsByEmail(anyString())).thenReturn(false);
    when(userRepository.save(any(User.class))).thenReturn(updatedUser);

    // WHEN
    UserDto actualUserinfoDto = userService.update(userDto);

    // THEN
    assertThat(actualUserinfoDto).usingRecursiveComparison().isEqualTo(updateDto);
    verify(userRepository, times(1)).findById(1);
    verify(userRepository, times(1)).existsByEmail("update@mail.com");
    verify(userRepository, times(1)).save(userCaptor.capture());
    assertThat(userCaptor.getValue()).usingRecursiveComparison().isEqualTo(updatedUser);
  }

  @Test
  void updateInfoWithNewPasswordEmailTest() throws Exception {
    // GIVEN
    UserDto userDto = new UserDto(1, "update","test", "user@mail.com","NewPassword", null, null);
    UserDto updateDto = new UserDto(1, "update","test", "user@mail.com",null, BigDecimal.ZERO, Role.USER.toString());
    User updatedUser = new User("update", "test", "user@mail.com", "NewEncoded", Role.USER);
    updatedUser.setUserId(1);
    when(userRepository.findById(anyInt())).thenReturn(Optional.of(userTest));
    when(passwordEncoder.encode(anyString())).thenReturn("NewEncoded");
    when(userRepository.save(any(User.class))).thenReturn(updatedUser);

    // WHEN
    UserDto actualUserinfoDto = userService.update(userDto);

    // THEN
    assertThat(actualUserinfoDto).usingRecursiveComparison().isEqualTo(updateDto);
    verify(userRepository, times(1)).findById(1);
    verify(passwordEncoder, times(1)).encode("NewPassword");
    verify(userRepository, times(1)).save(userCaptor.capture());
    assertThat(userCaptor.getValue()).usingRecursiveComparison().isEqualTo(updatedUser);
  }

  @Test
  void updateInfoWhenNotFoundTest() {
    // GIVEN
    UserDto userDto = new UserDto(9, "update","test", "update@mail.com",null, null, null);
    when(userRepository.findById(anyInt())).thenReturn(Optional.empty());

    // WHEN
    assertThatThrownBy(() -> userService.update(userDto))

        // THEN
        .isInstanceOf(ResourceNotFoundException.class)
        .hasMessageContaining("This user is not found");
    verify(userRepository, times(1)).findById(9);
    verify(userRepository, times(0)).existsByEmail(anyString());
    verify(userRepository, times(0)).save(any(User.class));
  }

  @Test
  void updateInfoWhenNewEmailAlreadyExistsTest() {
    // GIVEN
    UserDto userDto = new UserDto(1, "update","test", "existing@mail.com",null, null, null);
    when(userRepository.findById(anyInt())).thenReturn(Optional.of(userTest));
    when(userRepository.existsByEmail(anyString())).thenReturn(true);

    // WHEN
    assertThatThrownBy(() -> userService.update(userDto))

        // THEN
        .isInstanceOf(ResourceAlreadyExistsException.class)
        .hasMessageContaining("This email is already used");
    verify(userRepository, times(1)).findById(1);
    verify(userRepository, times(1)).existsByEmail("existing@mail.com");
    verify(userRepository, times(0)).save(any(User.class));
  }

  @Test
  void deleteByIdTest() throws Exception {
    // GIVEN
    when(userRepository.findById(anyInt())).thenReturn(Optional.of(userTest));

    // WHEN
    userService.deleteById(1);

    // THEN
    verify(userRepository, times(1)).findById(1);
    verify(userRepository, times(1)).delete(userTest);
  }

  @Test
  void deleteByIdWhenWalletNotEmptyTest() {
    // GIVEN
    userTest.credit(BigDecimal.ONE);
    when(userRepository.findById(anyInt())).thenReturn(Optional.of(userTest));

    // WHEN
    assertThatThrownBy(() -> userService.deleteById(1))

        // THEN
        .isInstanceOf(ForbiddenOperationException.class)
        .hasMessageContaining("The user can't delete account if wallet not empty");
    verify(userRepository, times(1)).findById(1);
    verify(userRepository, times(0)).delete(any(User.class));
  }

  @Test
  void deleteByIdWhenNotFoundTest() {
    // GIVEN
    when(userRepository.findById(anyInt())).thenReturn(Optional.empty());

    // WHEN
    assertThatThrownBy(() -> userService.deleteById(2))

        // THEN
        .isInstanceOf(ResourceNotFoundException.class)
        .hasMessageContaining("This user is not found");
    verify(userRepository, times(1)).findById(2);
    verify(userRepository, times(0)).delete(any(User.class));
  }
}

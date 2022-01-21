package com.openclassrooms.paymybuddy.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
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
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
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
    userTest = new User("user","test","user@mail.com","EncodedPwd", Role.USER, LocalDateTime.now());
    userTest.setUserId(1);
    userInfoDto = new UserDto(1, "user","test","user@mail.com",null,BigDecimal.ZERO, LocalDateTime.now(),true);
  }

  @DisplayName("Get all user should return a page of user DTO")
  @Test
  void getAllTest() {
    // GIVEN
    Pageable pageable = PageRequest.of(0,1);
    when(userRepository.findAll(any(Pageable.class))).thenReturn(new PageImpl<>(List.of(userTest)));

    // WHEN
    Page<UserDto> actualPage = userService.getAll(pageable);

    // THEN
    assertThat(actualPage.getContent()).usingRecursiveComparison().isEqualTo(List.of(userInfoDto));
    verify(userRepository, times(1)).findAll(pageable);
  }

  @DisplayName("Get all user when no user registered should return an empty page")
  @Test
  void getAllWhenEmptyTest() {
    // GIVEN
    Pageable pageable = PageRequest.of(0,1);
    when(userRepository.findAll(any(Pageable.class))).thenReturn(Page.empty());

    // WHEN
    Page<UserDto> actualPage = userService.getAll(pageable);

    // THEN
    assertThat(actualPage.getContent()).isEmpty();
    verify(userRepository, times(1)).findAll(pageable);
  }

  @DisplayName("Get a user should return the user DTO")
  @Test
  void getByIdTest() throws Exception {
    // GIVEN
    when(userRepository.findById(anyInt())).thenReturn(Optional.of(userTest));

    // WHEN
    UserDto actualDto = userService.getById(1);

    // THEN
    assertThat(actualDto).usingRecursiveComparison().isEqualTo(userInfoDto);
    verify(userRepository, times(1)).findById(1);
  }

  @DisplayName("Get a non existent user should throw an exception")
  @Test
  void getByIdWhenNotFoundTest() {
    // GIVEN
    when(userRepository.findById(anyInt())).thenReturn(Optional.empty());

    // WHEN
    assertThatThrownBy(() -> userService.getById(9))

        // THEN
        .isInstanceOf(ResourceNotFoundException.class)
        .hasMessageContaining("This user is not found");
    verify(userRepository, times(1)).findById(9);
  }

  @DisplayName("register a user should persist thr user with encoded password")
  @Test
  void registerTest() throws Exception {
    // GIVEN
    UserDto userDto = new UserDto(0,"user","test", "user@mail.com", "12345678", null, null, false);
    when(userRepository.existsByEmail(anyString())).thenReturn(false);
    when(passwordEncoder.encode(anyString())).thenReturn("EncodedPwd");
    when(userRepository.save(any(User.class))).thenReturn(userTest);

    // WHEN
    UserDto actualDto = userService.register(userDto);

    // THEN
    assertThat(actualDto).usingRecursiveComparison().isEqualTo(userInfoDto);
    verify(userRepository, times(1)).existsByEmail("user@mail.com");
    verify(passwordEncoder, times(1)).encode("12345678");
    verify(userRepository, times(1)).save(userCaptor.capture());
    assertThat(userCaptor.getValue()).usingRecursiveComparison()
        .ignoringFields("registrationDate", "userId").isEqualTo(userTest);
  }

  @DisplayName("register a user with an already existing password should throw an exception")
  @Test
  void registerWhenEmailAlreadyExistTest() {
    // GIVEN
    UserDto userDto = new UserDto(0,"user","test", "existing@mail.com", "12345678", null, null, false);
    when(userRepository.existsByEmail(anyString())).thenReturn(true);

    // WHEN
    assertThatThrownBy(() -> userService.register(userDto))

        // THEN
        .isInstanceOf(ResourceAlreadyExistsException.class)
        .hasMessageContaining("This email already exists");
    verify(userRepository, times(1)).existsByEmail("existing@mail.com");
    verify(passwordEncoder, times(0)).encode(anyString());
    verify(userRepository, times(0)).save(any(User.class));
  }

  @DisplayName("Update a user with same email and no password provided should update the user names")
  @Test
  void updateWithSameEmailTest() throws Exception {
    // GIVEN
    UserDto userDto = new UserDto(1, "update","test", "user@mail.com",null, null, null, false);
    UserDto updateDto = new UserDto(1, "update","test", "user@mail.com",null, BigDecimal.ZERO, LocalDateTime.now(),true);
    User updatedUser = new User("update", "test", "user@mail.com", "EncodedPwd", Role.USER, LocalDateTime.now());
    updatedUser.setUserId(1);
    when(userRepository.findById(anyInt())).thenReturn(Optional.of(userTest));
    when(userRepository.save(any(User.class))).thenReturn(updatedUser);

    // WHEN
    UserDto actualDto = userService.update(userDto);

    // THEN
    assertThat(actualDto).usingRecursiveComparison().isEqualTo(updateDto);
    verify(userRepository, times(1)).findById(1);
    verify(userRepository, times(1)).save(userCaptor.capture());
    assertThat(userCaptor.getValue()).usingRecursiveComparison().ignoringFields("registrationDate").isEqualTo(updatedUser);
  }

  @DisplayName("Update a user with new email and no password provided should update the user names and email")
  @Test
  void updateWithNewEmailTest() throws Exception {
    // GIVEN
    UserDto userDto = new UserDto(1, "update","test", "update@mail.com",null, null, null,false);
    UserDto
        updateDto = new UserDto(1, "update","test", "update@mail.com",null, BigDecimal.ZERO,LocalDateTime.now(),true);
    User updatedUser = new User("update", "test", "update@mail.com", "EncodedPwd", Role.USER, LocalDateTime.now());
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

  @DisplayName("Update a user with new email which already exists should throw and exception")
  @Test
  void updateWithNewEmailAlreadyExistingTest() {
    // GIVEN
    UserDto userDto = new UserDto(1, "update","test", "existing@mail.com",null, null, null,false);
    when(userRepository.findById(anyInt())).thenReturn(Optional.of(userTest));
    when(userRepository.existsByEmail(anyString())).thenReturn(true);

    // WHEN
    assertThatThrownBy(() -> userService.update(userDto))

        // THEN
        .isInstanceOf(ResourceAlreadyExistsException.class)
        .hasMessageContaining("This email already exists");
    verify(userRepository, times(1)).findById(1);
    verify(userRepository, times(1)).existsByEmail("existing@mail.com");
    verify(userRepository, times(0)).save(any(User.class));
  }

  @DisplayName("Update a user with same email and a password provided should update the user with new password encoded")
  @Test
  void updateInfoWithNewPasswordEmailTest() throws Exception {
    // GIVEN
    UserDto userDto = new UserDto(1, "update","test", "user@mail.com","NewPassword", null, null,false);
    UserDto updateDto = new UserDto(1, "update","test", "user@mail.com",null, BigDecimal.ZERO, LocalDateTime.now(),true);
    User updatedUser = new User("update", "test", "user@mail.com", "NewEncoded", Role.USER, LocalDateTime.now());
    updatedUser.setUserId(1);
    when(userRepository.findById(anyInt())).thenReturn(Optional.of(userTest));
    when(passwordEncoder.encode(anyString())).thenReturn("NewEncoded");
    when(userRepository.save(any(User.class))).thenReturn(updatedUser);

    // WHEN
    UserDto actualDto = userService.update(userDto);

    // THEN
    assertThat(actualDto).usingRecursiveComparison().isEqualTo(updateDto);
    verify(userRepository, times(1)).findById(1);
    verify(passwordEncoder, times(1)).encode("NewPassword");
    verify(userRepository, times(1)).save(userCaptor.capture());
    assertThat(userCaptor.getValue()).usingRecursiveComparison().ignoringFields("registrationDate").isEqualTo(updatedUser);
  }

  @DisplayName("Update a non existent user should throw an exception")
  @Test
  void updateInfoWhenNotFoundTest() {
    // GIVEN
    UserDto userDto = new UserDto(9, "update","test", "update@mail.com",null, null,null,false);
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

  @DisplayName("Setting account enabling")
  @Test
  void setAccountEnablingTest() throws Exception {
    // GIVEN
    when(userRepository.findById(anyInt())).thenReturn(Optional.of(userTest));

    // THEN
    userService.setAccountEnabling(1,true);
    assertThat(userTest.isEnabled()).isTrue();
    // THEN
    userService.setAccountEnabling(1,false);
    assertThat(userTest.isEnabled()).isFalse();

  }

  @DisplayName("Setting account enabling of a non existent user should throw an exception")
  @Test
  void setAccountEnablingWhenNotFound() {
    // GIVEN
    when(userRepository.findById(anyInt())).thenReturn(Optional.empty());

    // WHEN
    assertThatThrownBy(() -> userService.setAccountEnabling(9,true))

        // THEN
        .isInstanceOf(ResourceNotFoundException.class)
        .hasMessageContaining("This user is not found");
    verify(userRepository,times(0)).save(userTest);
  }

  @DisplayName("Delete a user")
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

  @DisplayName("Delete a user with non empty waller should throw an exception")
  @Test
  void deleteByIdWhenWalletNotEmptyTest() throws Exception {
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

  @DisplayName("Delete a non existent user should throw an exception")
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

  @DisplayName("Retrieving a user by Id")
  @Test
  void retrieveEntityWithIdTest() throws Exception {
    // GIVEN
    when(userRepository.findById(anyInt())).thenReturn(Optional.of(userTest));

    // WHEN
    User actualUser = userService.retrieveEntity(1);

    // THEN
    assertThat(actualUser).usingRecursiveComparison().isEqualTo(userTest);
    verify(userRepository, times(1)).findById(1);
  }

  @DisplayName("Retrieving by Id a non existent user should throw an exception")
  @Test
  void retrieveEntityWithIdWhenNotFoundTest() {
    // GIVEN
    when(userRepository.findById(anyInt())).thenReturn(Optional.empty());

    // WHEN
    assertThatThrownBy(() -> userService.retrieveEntity(2))

        // THEN
        .isInstanceOf(ResourceNotFoundException.class)
        .hasMessageContaining("This user is not found");
    verify(userRepository, times(1)).findById(2);
  }

  @DisplayName("Retrieving a user by email")
  @Test
  void retrieveEntityWithEmailTest() throws Exception {
    // GIVEN
    when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(userTest));

    // WHEN
    User actualUser = userService.retrieveEntity("user@mail.com");

    // THEN
    assertThat(actualUser).usingRecursiveComparison().isEqualTo(userTest);
    verify(userRepository, times(1)).findByEmail("user@mail.com");
  }

  @DisplayName("Retrieving by email a non existent user should throw an exception")
  @Test
  void retrieveEntityWithEmailWhenNotFoundTest() {
    // GIVEN
    when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());

    // WHEN
    assertThatThrownBy(() -> userService.retrieveEntity("notexisting@mail.com"))

        // THEN
        .isInstanceOf(ResourceNotFoundException.class)
        .hasMessageContaining("This user is not found");
    verify(userRepository, times(1)).findByEmail("notexisting@mail.com");
  }

  @DisplayName("Save a user")
  @Test
  void saveEntityTest() throws Exception {
    // GIVEN
    when(userRepository.existsById(anyInt())).thenReturn(true);

    // WHEN
    userService.saveEntity(userTest);

    // THEN
    verify(userRepository, times(1)).existsById(1);
    verify(userRepository, times(1)).save(userTest);
  }

  @DisplayName("Save a non existent user should throw an exception")
  @Test
  void saveEntityWhenNotFoundTest() {
    // GIVEN
    User newUser = new User("new","test", "new@mail.com", "password",null, null);
    when(userRepository.existsById(anyInt())).thenReturn(false);

    // WHEN
    assertThatThrownBy(() -> userService.saveEntity(newUser))

        // THEN
        .isInstanceOf(ResourceNotFoundException.class)
        .hasMessageContaining("This user is not found");
    verify(userRepository, times(1)).existsById(0);
    verify(userRepository, times(0)).save(any(User.class));
  }

}

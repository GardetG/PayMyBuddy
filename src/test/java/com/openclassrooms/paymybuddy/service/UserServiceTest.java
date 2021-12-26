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
import com.openclassrooms.paymybuddy.dto.UserSubscriptionDto;
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

@SpringBootTest
class UserServiceTest {

  @Autowired
  private UserService userService;

  @MockBean
  UserRepository userRepository;

  @Captor
  ArgumentCaptor<User> userCaptor;

  private User userTest;
  private UserInfoDto userInfoDto;

  @BeforeEach
  void setUp() {
    userTest = new User(0,"test","test","test@mail.com","12345678", BigDecimal.ZERO);
    userInfoDto = new UserInfoDto(0, "test","test","test@mail.com",BigDecimal.ZERO);
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
  void subscribeTest() throws Exception {
    // GIVEN
    UserSubscriptionDto subscriptionDto = new UserSubscriptionDto("test","test", "test@mail.com", "12345678");
    when(userRepository.existsByEmail(anyString())).thenReturn(false);
    when(userRepository.save(any(User.class))).thenReturn(userTest);

    // WHEN
    UserInfoDto actualUserinfoDto = userService.subscribe(subscriptionDto);

    // THEN
    assertThat(actualUserinfoDto).usingRecursiveComparison().isEqualTo(userInfoDto);
    verify(userRepository, times(1)).existsByEmail("test@mail.com");
    verify(userRepository, times(1)).save(userCaptor.capture());
    assertThat(userCaptor.getValue()).usingRecursiveComparison().isEqualTo(userTest);
  }

  @Test
  void subscribeWhenEmailAlreadyExistTest() {
    // GIVEN
    UserSubscriptionDto subscriptionDto = new UserSubscriptionDto("test","test", "test@mail.com", "12345678");
    when(userRepository.existsByEmail(anyString())).thenReturn(true);

    // WHEN
    assertThatThrownBy(() -> userService.subscribe(subscriptionDto))

        // THEN
        .isInstanceOf(EmailAlreadyExistsException.class)
        .hasMessageContaining("This email is already used");
    verify(userRepository, times(1)).existsByEmail("test@mail.com");
    verify(userRepository, times(0)).save(any(User.class));
  }

}

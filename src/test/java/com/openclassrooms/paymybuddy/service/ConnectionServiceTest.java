package com.openclassrooms.paymybuddy.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.openclassrooms.paymybuddy.dto.ConnectionDto;
import com.openclassrooms.paymybuddy.exception.ResourceNotFoundException;
import com.openclassrooms.paymybuddy.model.Role;
import com.openclassrooms.paymybuddy.model.User;
import com.openclassrooms.paymybuddy.repository.UserRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

@SpringBootTest
class ConnectionServiceTest {

  @Autowired
  private ConnectionService connectionService;

  @MockBean
  private UserRepository userRepository;

  private User user1Test;
  private User user2Test;
  private ConnectionDto connectionDto;

  @BeforeEach
  void setUp() {
    user1Test = new User("user1","test","user1@mail.com","12345678", Role.USER);
    user1Test.setUserId(1);
    user2Test = new User("user2","test","user2@mail.com","12345678", Role.USER);
    user2Test.setUserId(2);
    connectionDto = new ConnectionDto(2,"user2","test","user2@mail.com");
  }

  @Test
  void getAllFromUserTest() throws Exception {
    // GIVEN
    user1Test.addConnection(user2Test);
    when(userRepository.findById(anyInt())).thenReturn(Optional.of(user1Test));

    // WHEN
    List<ConnectionDto> actualListConnectionstDto = connectionService.getAllFromUser(1);

    // THEN
    assertThat(actualListConnectionstDto).usingRecursiveComparison().isEqualTo(List.of(connectionDto));
    verify(userRepository, times(1)).findById(1);
  }

  @Test
  void getAllFromUserWhenEmptyTest() throws Exception {
    // GIVEN
    when(userRepository.findById(anyInt())).thenReturn(Optional.of(user1Test));

    // WHEN
    List<ConnectionDto> actualListConnectionstDto = connectionService.getAllFromUser(1);

    // THEN
    assertThat(actualListConnectionstDto).isEmpty();
    verify(userRepository, times(1)).findById(1);
  }

  @Test
  void getAllFromUserWhenUserNotFoundTest() {
    // GIVEN
    when(userRepository.findById(anyInt())).thenReturn(Optional.empty());

    // WHEN
    assertThatThrownBy(() -> connectionService.getAllFromUser(9))

        // THEN
        .isInstanceOf(ResourceNotFoundException.class)
        .hasMessageContaining("This user is not found");
    verify(userRepository, times(1)).findById(9);
  }

}

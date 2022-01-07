package com.openclassrooms.paymybuddy.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.openclassrooms.paymybuddy.dto.BankAccountDto;
import com.openclassrooms.paymybuddy.dto.ConnectionDto;
import com.openclassrooms.paymybuddy.exception.ForbbidenOperationException;
import com.openclassrooms.paymybuddy.exception.ResourceAlreadyExistsException;
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

  @Test
  void addToUserTest() throws Exception {
    // GIVEN
    ConnectionDto requestDto = new ConnectionDto(0, null,null,"user2@mail.com");
    when(userRepository.findById(anyInt())).thenReturn(Optional.of(user1Test));
    when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user2Test));
    when(userRepository.save(any(User.class))).thenReturn(user1Test);

    // WHEN
    ConnectionDto actualConnectionDto = connectionService.addToUser(1,requestDto);

    // THEN
    assertThat(actualConnectionDto).usingRecursiveComparison().isEqualTo(connectionDto);
    assertThat(user1Test.getConnections().size()).isEqualTo(1);
    verify(userRepository, times(1)).findById(1);
    verify(userRepository, times(1)).findByEmail("user2@mail.com");
    verify(userRepository,times(1)).save(any(User.class));
  }

  @Test
  void addToUserWhenUserNotFoundTest() {
    // GIVEN
    ConnectionDto requestDto = new ConnectionDto(0, null,null,"user2@mail.com");
    when(userRepository.findById(anyInt())).thenReturn(Optional.empty());

    // WHEN
    assertThatThrownBy(() -> connectionService.addToUser(9,requestDto))

        // THEN
        .isInstanceOf(ResourceNotFoundException.class)
        .hasMessageContaining("This user is not found");
    verify(userRepository, times(1)).findById(9);
    verify(userRepository, times(0)).findByEmail(anyString());
    verify(userRepository,times(0)).save(any(User.class));
  }

  @Test
  void addToUserWhenConnectionNotFoundTest() {
    // GIVEN
    ConnectionDto requestDto = new ConnectionDto(0, null,null,"NotExisting@mail.com");
    when(userRepository.findById(anyInt())).thenReturn(Optional.of(user1Test));
    when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());

    // WHEN
    assertThatThrownBy(() -> connectionService.addToUser(1,requestDto))

        // THEN
        .isInstanceOf(ResourceNotFoundException.class)
        .hasMessageContaining("This user is not found");
    verify(userRepository, times(1)).findById(1);
    verify(userRepository, times(1)).findByEmail("NotExisting@mail.com");
    verify(userRepository,times(0)).save(any(User.class));
  }

  @Test
  void addToUserWithAlreadyAddedConnectionTest() throws Exception{
    // GIVEN
    user1Test.addConnection(user2Test);
    ConnectionDto requestDto = new ConnectionDto(0, null,null,"user2@mail.com");
    when(userRepository.findById(anyInt())).thenReturn(Optional.of(user1Test));
    when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user2Test));

    // WHEN
    assertThatThrownBy(() -> connectionService.addToUser(1,requestDto))

        // THEN
        .isInstanceOf(ResourceAlreadyExistsException.class)
        .hasMessageContaining("This connection already exists");
    verify(userRepository, times(1)).findById(1);
    verify(userRepository, times(1)).findByEmail("user2@mail.com");
    verify(userRepository,times(0)).save(any(User.class));
  }

  @Test
  void addToUserWithHimselfAsConnectionTest() {
    // GIVEN
    ConnectionDto requestDto = new ConnectionDto(0, null,null,"user1@mail.com");
    when(userRepository.findById(anyInt())).thenReturn(Optional.of(user1Test));

    // WHEN
    assertThatThrownBy(() -> connectionService.addToUser(1,requestDto))

        // THEN
        .isInstanceOf(ForbbidenOperationException.class)
        .hasMessageContaining("The user can't add himself as connection");
    verify(userRepository, times(1)).findById(1);
    verify(userRepository, times(0)).findByEmail(anyString());
    verify(userRepository,times(0)).save(any(User.class));
  }

  @Test
  void removeFromUserTest() throws Exception {
    // GIVEN
    user1Test.addConnection(user2Test);
    when(userRepository.findById(anyInt())).thenReturn(Optional.of(user1Test));
    when(userRepository.save(any(User.class))).thenReturn(user1Test);

    // WHEN
    connectionService.removeFromUser(1,2);

    // THEN
    assertThat(user1Test.getConnections()).isEmpty();
    verify(userRepository, times(1)).findById(1);
    verify(userRepository,times(1)).save(any(User.class));
  }

  @Test
  void removeFromUserWhenUserNotFoundTest() {
    // GIVEN
    when(userRepository.findById(anyInt())).thenReturn(Optional.empty());

    // WHEN
    assertThatThrownBy(() -> connectionService.removeFromUser(9,1))

        // THEN
        .isInstanceOf(ResourceNotFoundException.class)
        .hasMessageContaining("This user is not found");
    verify(userRepository, times(1)).findById(9);
    verify(userRepository,times(0)).save(any(User.class));
  }

  @Test
  void removeFromUserWhenConnectionNotFoundTest() {
    // GIVEN
    when(userRepository.findById(anyInt())).thenReturn(Optional.of(user1Test));

    // WHEN
    assertThatThrownBy(() -> connectionService.removeFromUser(1,9))

        // THEN
        .isInstanceOf(ResourceNotFoundException.class)
        .hasMessageContaining("This connection is not found");
    verify(userRepository, times(1)).findById(1);
    verify(userRepository,times(0)).save(any(User.class));
  }

}

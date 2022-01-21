package com.openclassrooms.paymybuddy.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.openclassrooms.paymybuddy.dto.ConnectionDto;
import com.openclassrooms.paymybuddy.exception.ForbiddenOperationException;
import com.openclassrooms.paymybuddy.exception.ResourceAlreadyExistsException;
import com.openclassrooms.paymybuddy.exception.ResourceNotFoundException;
import com.openclassrooms.paymybuddy.model.Role;
import com.openclassrooms.paymybuddy.model.User;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@SpringBootTest
class ConnectionServiceTest {

  @Autowired
  private ConnectionService connectionService;

  @MockBean
  private UserService userService;

  private User user1Test;
  private User user2Test;
  private ConnectionDto connectionDto;

  @BeforeEach
  void setUp() {
    user1Test = new User("user1","test","user1@mail.com","12345678", Role.USER, LocalDateTime.now());
    user1Test.setUserId(1);
    user2Test = new User("user2","test","user2@mail.com","12345678", Role.USER, LocalDateTime.now());
    user2Test.setUserId(2);
    connectionDto = new ConnectionDto(2,"user2","test","user2@mail.com");
  }

  @DisplayName("Get all connections of a user should return a page of DTO")
  @Test
  void getAllFromUserTest() throws Exception {
    // GIVEN
    user1Test.addConnection(user2Test);
    Pageable pageable = PageRequest.of(0,1);
    when(userService.retrieveEntity(anyInt())).thenReturn(user1Test);

    // WHEN
    Page<ConnectionDto> actuaPage = connectionService.getAllFromUser(1, pageable);

    // THEN
    assertThat(actuaPage.getContent()).usingRecursiveComparison().isEqualTo(List.of(connectionDto));
    verify(userService, times(1)).retrieveEntity(1);
  }

  @DisplayName("Get all connections of a user when connections is empty should return an empty page")
  @Test
  void getAllFromUserWhenEmptyTest() throws Exception {
    // GIVEN
    Pageable pageable = PageRequest.of(0,1);
    when(userService.retrieveEntity(anyInt())).thenReturn(user1Test);

    // WHEN
    Page<ConnectionDto> actuaPage = connectionService.getAllFromUser(1, pageable);

    // THEN
    assertThat(actuaPage).isEmpty();
    verify(userService, times(1)).retrieveEntity(1);
  }

  @DisplayName("Get all connections of a non existent user should throw an exception")
  @Test
  void getAllFromUserWhenUserNotFoundTest() throws Exception {
    // GIVEN
    Pageable pageable = PageRequest.of(0,1);
    when(userService.retrieveEntity(anyInt())).thenThrow(
        new ResourceNotFoundException("This user is not found"));

    // WHEN
    assertThatThrownBy(() -> connectionService.getAllFromUser(9, pageable))

        // THEN
        .isInstanceOf(ResourceNotFoundException.class)
        .hasMessageContaining("This user is not found");
    verify(userService, times(1)).retrieveEntity(9);
  }

  @DisplayName("Add a connections to a user")
  @Test
  void addToUserTest() throws Exception {
    // GIVEN
    ConnectionDto requestDto = new ConnectionDto(0, null,null,"user2@mail.com");
    when(userService.retrieveEntity(anyInt())).thenReturn(user1Test);
    when(userService.retrieveEntity(anyString())).thenReturn(user2Test);
    when(userService.saveEntity(any(User.class))).thenReturn(user1Test);

    // WHEN
    ConnectionDto actualConnectionDto = connectionService.addToUser(1,requestDto);

    // THEN
    assertThat(actualConnectionDto).usingRecursiveComparison().isEqualTo(connectionDto);
    assertThat(user1Test.getConnections().size()).isEqualTo(1);
    verify(userService, times(1)).retrieveEntity(1);
    verify(userService, times(1)).retrieveEntity("user2@mail.com");
    verify(userService,times(1)).saveEntity(any(User.class));
  }

  @DisplayName("Add a connection to a non existent user should throw an exception")
  @Test
  void addToUserWhenUserNotFoundTest() throws Exception {
    // GIVEN
    ConnectionDto requestDto = new ConnectionDto(0, null,null,"user2@mail.com");
    when(userService.retrieveEntity(anyInt())).thenThrow(
        new ResourceNotFoundException("This user is not found"));

    // WHEN
    assertThatThrownBy(() -> connectionService.addToUser(9,requestDto))

        // THEN
        .isInstanceOf(ResourceNotFoundException.class)
        .hasMessageContaining("This user is not found");
    verify(userService, times(1)).retrieveEntity(9);
    verify(userService, times(0)).retrieveEntity(anyString());
    verify(userService,times(0)).saveEntity(any(User.class));
  }

  @DisplayName("Add a non existent user as connection to a user should throw an exception")
  @Test
  void addToUserWhenConnectionNotFoundTest() throws Exception {
    // GIVEN
    ConnectionDto requestDto = new ConnectionDto(0, null,null,"NotExisting@mail.com");
    when(userService.retrieveEntity(anyInt())).thenReturn(user1Test);
    when(userService.retrieveEntity(anyString())).thenThrow(
        new ResourceNotFoundException("This user is not found"));

    // WHEN
    assertThatThrownBy(() -> connectionService.addToUser(1,requestDto))

        // THEN
        .isInstanceOf(ResourceNotFoundException.class)
        .hasMessageContaining("This user is not found");
    verify(userService, times(1)).retrieveEntity(1);
    verify(userService, times(1)).retrieveEntity("NotExisting@mail.com");
    verify(userService,times(0)).saveEntity(any(User.class));
  }

  @DisplayName("Add a connection already added to a user should throw an exception")
  @Test
  void addToUserWithAlreadyAddedConnectionTest() throws Exception{
    // GIVEN
    user1Test.addConnection(user2Test);
    ConnectionDto requestDto = new ConnectionDto(0, null,null,"user2@mail.com");
    when(userService.retrieveEntity(anyInt())).thenReturn(user1Test);
    when(userService.retrieveEntity(anyString())).thenReturn(user2Test);

    // WHEN
    assertThatThrownBy(() -> connectionService.addToUser(1,requestDto))

        // THEN
        .isInstanceOf(ResourceAlreadyExistsException.class)
        .hasMessageContaining("This connection already exists");
    verify(userService, times(1)).retrieveEntity(1);
    verify(userService, times(1)).retrieveEntity("user2@mail.com");
    verify(userService,times(0)).saveEntity(any(User.class));
  }

  @DisplayName("Add himself as connection should throw an exception")
  @Test
  void addToUserWithHimselfAsConnectionTest() throws Exception {
    // GIVEN
    ConnectionDto requestDto = new ConnectionDto(0, null,null,"user1@mail.com");
    when(userService.retrieveEntity(anyInt())).thenReturn(user1Test);

    // WHEN
    assertThatThrownBy(() -> connectionService.addToUser(1,requestDto))

        // THEN
        .isInstanceOf(ForbiddenOperationException.class)
        .hasMessageContaining("The user can't add himself as connection");
    verify(userService, times(1)).retrieveEntity(1);
    verify(userService, times(0)).retrieveEntity(anyString());
    verify(userService,times(0)).saveEntity(any(User.class));
  }

  @DisplayName("Remove a connection from a user")
  @Test
  void removeFromUserTest() throws Exception {
    // GIVEN
    user1Test.addConnection(user2Test);
    when(userService.retrieveEntity(anyInt())).thenReturn(user1Test);
    when(userService.saveEntity(any(User.class))).thenReturn(user1Test);

    // WHEN
    connectionService.removeFromUser(1,2);

    // THEN
    assertThat(user1Test.getConnections()).isEmpty();
    verify(userService, times(1)).retrieveEntity(1);
    verify(userService,times(1)).saveEntity(any(User.class));
  }

  @DisplayName("Remove a connection from a non existent user should throw an exception")
  @Test
  void removeFromUserWhenUserNotFoundTest() throws Exception {
    // GIVEN
    when(userService.retrieveEntity(anyInt())).thenThrow(
        new ResourceNotFoundException("This user is not found"));

    // WHEN
    assertThatThrownBy(() -> connectionService.removeFromUser(9,1))

        // THEN
        .isInstanceOf(ResourceNotFoundException.class)
        .hasMessageContaining("This user is not found");
    verify(userService, times(1)).retrieveEntity(9);
    verify(userService,times(0)).saveEntity(any(User.class));
  }

  @DisplayName("Remove a non existent connection from a user should throw an exception")
  @Test
  void removeFromUserWhenConnectionNotFoundTest() throws Exception {
    // GIVEN
    when(userService.retrieveEntity(anyInt())).thenReturn(user1Test);

    // WHEN
    assertThatThrownBy(() -> connectionService.removeFromUser(1,9))

        // THEN
        .isInstanceOf(ResourceNotFoundException.class)
        .hasMessageContaining("This connection is not found");
    verify(userService, times(1)).retrieveEntity(1);
    verify(userService,times(0)).saveEntity(any(User.class));
  }

}

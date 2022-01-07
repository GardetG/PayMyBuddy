package com.openclassrooms.paymybuddy.controller;

import static org.hamcrest.CoreMatchers.is;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.openclassrooms.paymybuddy.dto.ConnectionDto;
import com.openclassrooms.paymybuddy.exception.ResourceNotFoundException;
import com.openclassrooms.paymybuddy.model.Role;
import com.openclassrooms.paymybuddy.model.User;
import com.openclassrooms.paymybuddy.service.ConnectionService;
import com.openclassrooms.paymybuddy.service.CredentialsService;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(value = ConnectionController.class)
class ConnectionControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private ConnectionService connectionService;

  @MockBean
  private CredentialsService credentialsService;

  private ConnectionDto connectionDtoTest;
  private User userTest;
  private User adminTest;

  @BeforeEach
  void setUp() {
    connectionDtoTest = new ConnectionDto(2,"user2","test","user2@mail.com");
    userTest = new User("user1","test","user1@mail.com","password", Role.USER);
    userTest.setUserId(1);
    adminTest = new User("test","test","test@mail.com","password", Role.ADMIN);
  }

  @Test
  void getAllFromUserTest() throws Exception {
    // GIVEN
    when(connectionService.getAllFromUser(anyInt())).thenReturn(List.of(connectionDtoTest));

    // WHEN
    mockMvc.perform(get("/users/1/connections").with(user(userTest)))

        // THEN
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].connectionId", is(2)))
        .andExpect(jsonPath("$[0].firstname", is("user2")))
        .andExpect(jsonPath("$[0].lastname", is("test")))
        .andExpect(jsonPath("$[0].email").doesNotExist());
    verify(connectionService, times(1)).getAllFromUser(1);
  }

  @Test
  void getAllFromUserWhenNotFoundTest() throws Exception {
    // GIVEN
    when(connectionService.getAllFromUser(anyInt())).thenThrow(
        new ResourceNotFoundException("This user is not found"));

    // WHEN
    mockMvc.perform(get("/users/9/connections").with(user(adminTest)))

        // THEN
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$", is("This user is not found")));
    verify(connectionService, times(1)).getAllFromUser(9);
  }

  @Test
  void getAllFromUserNotAuthenticateTest() throws Exception {
    // GIVEN

    // WHEN
    mockMvc.perform(get("/users/1/connections"))

        // THEN
        .andExpect(status().isUnauthorized());
    verify(connectionService, times(0)).getAllFromUser(anyInt());
  }

  @Test
  void getAllFromUserWhenAuthenticateButIdNotMatchingTest() throws Exception {
    // GIVEN

    // WHEN
    mockMvc.perform(get("/users/2/connections").with(user(userTest)))

        // THEN
        .andExpect(status().isForbidden());
    verify(connectionService, times(0)).getAllFromUser(anyInt());
  }


  @Test
  void removeFromUserTest() throws Exception {
    // GIVEN

    // WHEN
    mockMvc.perform(delete("/users/1/connections/2").with(user(userTest)))

        // THEN
        .andExpect(status().isNoContent());
    verify(connectionService, times(1)).removeFromUser(1,2);
  }

  @Test
  void removeFromUserWhenUserNotFoundTest() throws Exception {
    // GIVEN
    doThrow(new ResourceNotFoundException("This user is not found")).when(connectionService)
        .removeFromUser(anyInt(),anyInt());

    // WHEN
    mockMvc.perform(delete("/users/9/connections/1").with(user(adminTest)))

        // THEN
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$", is("This user is not found")));
    verify(connectionService, times(1)).removeFromUser(9,1);
  }


  @Test
  void removeFromUserWhenAccountNotFoundTest() throws Exception {
    // GIVEN
    doThrow(new ResourceNotFoundException("This account is not found")).when(connectionService)
        .removeFromUser(anyInt(),anyInt());

    // WHEN
    mockMvc.perform(delete("/users/1/connections/9").with(user(userTest)))

        // THEN
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$", is("This account is not found")));
    verify(connectionService, times(1)).removeFromUser(1,9);
  }

  @Test
  void removeFromUserWhenNotAuthenticateTest() throws Exception {
    // GIVEN

    // WHEN
    mockMvc.perform(delete("/users/1/bankaccounts/9"))

        // THEN
        .andExpect(status().isUnauthorized());
    verify(connectionService, times(0)).removeFromUser(anyInt(),anyInt());
  }

  @Test
  void removeFromUserWhenAuthenticateButIdNotMatchingTest() throws Exception {
    // GIVEN

    // WHEN
    mockMvc.perform(delete("/users/2/bankaccounts/9").with(user(userTest)))

        // THEN
        .andExpect(status().isForbidden());
    verify(connectionService, times(0)).removeFromUser(anyInt(),anyInt());
  }

}

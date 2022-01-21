package com.openclassrooms.paymybuddy.controller;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.hamcrest.CoreMatchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.openclassrooms.paymybuddy.config.PageableConfiguration;
import com.openclassrooms.paymybuddy.dto.ConnectionDto;
import com.openclassrooms.paymybuddy.exception.ForbiddenOperationException;
import com.openclassrooms.paymybuddy.exception.ResourceAlreadyExistsException;
import com.openclassrooms.paymybuddy.exception.ResourceNotFoundException;
import com.openclassrooms.paymybuddy.model.Role;
import com.openclassrooms.paymybuddy.model.User;
import com.openclassrooms.paymybuddy.service.ConnectionService;
import com.openclassrooms.paymybuddy.service.CredentialsService;
import java.time.LocalDateTime;
import java.util.List;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(value = ConnectionController.class)
@Import(PageableConfiguration.class)
class ConnectionControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private ConnectionService connectionService;

  @MockBean
  private CredentialsService credentialsService;

  @Captor
  ArgumentCaptor<ConnectionDto> DtoCaptor;

  private ConnectionDto connectionDtoTest;
  private User userTest;
  private User adminTest;
  JSONObject JSONparam;

  @BeforeEach
  void setUp() {
    connectionDtoTest = new ConnectionDto(2,"user2","test","user2@mail.com");
    userTest = new User("user1","test","user1@mail.com","password", Role.USER, LocalDateTime.now());
    userTest.setUserId(1);
    adminTest = new User("test","test","test@mail.com","password", Role.ADMIN, LocalDateTime.now());
    JSONparam = new JSONObject();
  }

  @DisplayName("GET all user connections should return 200 with page of connection DTO")
  @Test
  void getAllFromUserTest() throws Exception {
    // GIVEN
    when(connectionService.getAllFromUser(anyInt(), any(Pageable.class))).thenReturn(new PageImpl<>(List.of(connectionDtoTest)));

    // WHEN
    mockMvc.perform(get("/users/1/connections").with(user(userTest)))

        // THEN
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content.[0].connectionId", is(2)))
        .andExpect(jsonPath("$.content.[0].firstname", is("user2")))
        .andExpect(jsonPath("$.content.[0].lastname", is("test")))
        .andExpect(jsonPath("$.content.[0].email").doesNotExist());
    verify(connectionService, times(1)).getAllFromUser(1, Pageable.unpaged());
  }

  @DisplayName("GET all user connection of non existent user should return 404")
  @Test
  void getAllFromUserWhenNotFoundTest() throws Exception {
    // GIVEN
    when(connectionService.getAllFromUser(anyInt(), any(Pageable.class))).thenThrow(
        new ResourceNotFoundException("This user is not found"));

    // WHEN
    mockMvc.perform(get("/users/9/connections").with(user(adminTest)))

        // THEN
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$", is("This user is not found")));
    verify(connectionService, times(1)).getAllFromUser(9, Pageable.unpaged());
  }

  @DisplayName("GET all user connection from an other user should return 403")
  @Test
  void getAllFromUserWhenIdNotMatchingTest() throws Exception {
    // GIVEN

    // WHEN
    mockMvc.perform(get("/users/2/connections").with(user(userTest)))

        // THEN
        .andExpect(status().isForbidden());
    verify(connectionService, times(0)).getAllFromUser(anyInt(), any(Pageable.class));
  }

  @DisplayName("POST connection to user should return 201 with created connection DTO")
  @Test
  void addToUserTest() throws Exception {
    // GIVEN
    JSONparam.put("email","user2@mail.com");
    when(connectionService.addToUser(anyInt(), any(ConnectionDto.class))).thenReturn(connectionDtoTest);

    // WHEN
    mockMvc.perform(post("/users/1/connections").with(user(userTest))
            .with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(JSONparam.toString()))

        // THEN
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.connectionId", is(2)))
        .andExpect(jsonPath("$.firstname", is("user2")))
        .andExpect(jsonPath("$.lastname", is("test")))
        .andExpect(jsonPath("$.email").doesNotExist());
    verify(connectionService, times(1)).addToUser(anyInt(),DtoCaptor.capture());
    ConnectionDto expectedDto = new ConnectionDto(0,null,null,"user2@mail.com");
    assertThat(DtoCaptor.getValue()).usingRecursiveComparison().isEqualTo(expectedDto);
  }

  @DisplayName("POST invalid connection to user should return 422")
  @Test
  void addToUserWithInvalidConnectionEmailTest() throws Exception {
    // GIVEN
    JSONparam.put("email","usermail");

    // WHEN
    mockMvc.perform(post("/users/1/connections").with(user(userTest))
            .with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(JSONparam.toString()))

        // THEN
        .andExpect(status().isUnprocessableEntity())
        .andExpect(jsonPath("$.email", is("Email should be a valid email address")));
    verify(connectionService, times(0)).addToUser(anyInt(),any(ConnectionDto.class));
  }

  @DisplayName("POST connection already added to user should return 409")
  @Test
  void addToUserWhenUserAlreadyAddedTest() throws Exception {
    // GIVEN
    JSONparam.put("email","user2@mail.com");
    when(connectionService.addToUser(anyInt(), any(ConnectionDto.class))).thenThrow(
        new ResourceAlreadyExistsException("This connection already exists"));

    // WHEN
    mockMvc.perform(post("/users/1/connections").with(user(userTest))
            .with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(JSONparam.toString()))

        // THEN
        .andExpect(status().isConflict())
        .andExpect(jsonPath("$", is("This connection already exists")));
    verify(connectionService, times(1)).addToUser(anyInt(),any(ConnectionDto.class));
  }

  @DisplayName("POST himself as connection to user should return 409")
  @Test
  void addToUserWhenUserAddHimselfTest() throws Exception {
    // GIVEN
    JSONparam.put("email","user1@mail.com");
    when(connectionService.addToUser(anyInt(), any(ConnectionDto.class))).thenThrow(
        new ForbiddenOperationException("The user can't add himself as connection"));

    // WHEN
    mockMvc.perform(post("/users/1/connections").with(user(userTest))
            .with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(JSONparam.toString()))

        // THEN
        .andExpect(status().isConflict())
        .andExpect(jsonPath("$", is("The user can't add himself as connection")));
    verify(connectionService, times(1)).addToUser(anyInt(),any(ConnectionDto.class));
  }

  @DisplayName("POST connection to other user should return 403")
  @Test
  void addToUserWhenIdNotMatchingTest() throws Exception {
    // GIVEN
    JSONparam.put("email","user1@mail.com");

    // WHEN
    mockMvc.perform(get("/users/2/connections").with(user(userTest))
            .with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(JSONparam.toString()))

        // THEN
        .andExpect(status().isForbidden());
    verify(connectionService, times(0)).addToUser(anyInt(),any(ConnectionDto.class));
  }

  @DisplayName("DELETE connection from a user should return 204")
  @Test
  void removeFromUserTest() throws Exception {
    // GIVEN

    // WHEN
    mockMvc.perform(delete("/users/1/connections/2").with(user(userTest)))

        // THEN
        .andExpect(status().isNoContent());
    verify(connectionService, times(1)).removeFromUser(1,2);
  }

  @DisplayName("DELETE connection from a user when user or connection non exists should return 404")
  @Test
  void removeFromUserWhenUserNotFoundTest() throws Exception {
    // GIVEN
    doThrow(new ResourceNotFoundException("This resource is not found")).when(connectionService)
        .removeFromUser(anyInt(),anyInt());

    // WHEN
    mockMvc.perform(delete("/users/9/connections/9").with(user(adminTest)))

        // THEN
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$", is("This resource is not found")));
    verify(connectionService, times(1)).removeFromUser(9,9);
  }

  @DisplayName("DELETE connection from an other user should return 403")
  @Test
  void removeFromUserWhenAuthenticateButIdNotMatchingTest() throws Exception {
    // GIVEN

    // WHEN
    mockMvc.perform(delete("/users/2/connections/9").with(user(userTest)))

        // THEN
        .andExpect(status().isForbidden());
    verify(connectionService, times(0)).removeFromUser(anyInt(),anyInt());
  }

}

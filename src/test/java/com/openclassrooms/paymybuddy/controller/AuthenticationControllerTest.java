package com.openclassrooms.paymybuddy.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.openclassrooms.paymybuddy.dto.UserDto;
import com.openclassrooms.paymybuddy.exception.ResourceAlreadyExistsException;
import com.openclassrooms.paymybuddy.exception.ResourceNotFoundException;
import com.openclassrooms.paymybuddy.model.Role;
import com.openclassrooms.paymybuddy.model.User;
import com.openclassrooms.paymybuddy.service.CredentialsService;
import com.openclassrooms.paymybuddy.service.UserService;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(value = AuthenticationController.class)
class AuthenticationControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private UserService userService;

  @MockBean
  private CredentialsService credentialsService;

  @Captor
  ArgumentCaptor<UserDto> subscriptionCaptor;

  private User userTest;
  private User adminTest;
  private JSONObject jsonParam;

  @BeforeEach
  void setUp() {
    LocalDateTime date = LocalDateTime.of(2000,1,1,0,0);
    userTest = new User("test", "test", "test@mail.com", "password", Role.USER, date);
    userTest.setUserId(1);
    adminTest = new User("test","test","test@mail.com","password", Role.ADMIN, date);
    jsonParam = new JSONObject();
  }

  @DisplayName("POST subscription should return 201 with user registered")
  @Test
  void postSubscriptionTest() throws Exception {
    // GIVEN
    jsonParam.put("firstname","test")
        .put("lastname","test")
        .put("email","test@mail.com")
        .put("password","password");
    UserDto userDto = new UserDto(1, "test", "test", "test@mail.com", null,BigDecimal.ZERO, LocalDateTime.of(2000,1,1,0,0));
    when(userService.register(any(UserDto.class))).thenReturn(userDto);

    // WHEN
    mockMvc.perform(post("/register")
            .with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(jsonParam.toString()))

        // THEN
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.userId", is(1)))
        .andExpect(jsonPath("$.firstname", is("test")))
        .andExpect(jsonPath("$.lastname", is("test")))
        .andExpect(jsonPath("$.email", is("test@mail.com")))
        .andExpect(jsonPath("$.password").doesNotExist())
        .andExpect(jsonPath("$.wallet", is(0)))
        .andExpect(jsonPath("$.registrationDate", is("2000-01-01 at 00:00")));
    verify(userService, times(1)).register(subscriptionCaptor.capture());
    UserDto expectedDto = new UserDto(0,"test","test", "test@mail.com","password",null,null);
    assertThat(subscriptionCaptor.getValue()).usingRecursiveComparison().isEqualTo(expectedDto);
  }

  @DisplayName("POST subscription with invalid DTO should return 422")
  @Test
  void postInvalidSubscriptionTest() throws Exception {
    // GIVEN
    jsonParam.put("firstname","").put("lastname","")
        .put("email","invalidmail").put("password",null);

    // WHEN
    mockMvc.perform(post("/register")
            .with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(jsonParam.toString()))

        // THEN
        .andExpect(status().isUnprocessableEntity())
        .andExpect(jsonPath("$.firstname", is("Firstname is mandatory")))
        .andExpect(jsonPath("$.lastname", is("Lastname is mandatory")))
        .andExpect(jsonPath("$.email", is("Email should be a valid email address")))
        .andExpect(jsonPath("$.password", is("Password is mandatory")));
    verify(userService, times(0)).register(any(UserDto.class));
  }

  @DisplayName("POST subscription with already existing email should return 409")
  @Test
  void postSubscriptionWithAlreadyUsedEmailTest() throws Exception {
    // GIVEN
    jsonParam.put("firstname","test")
        .put("lastname","test")
        .put("email","existing@mail.com"
        ).put("password","password");
    when(userService.register(any(UserDto.class))).thenThrow(
        new ResourceAlreadyExistsException("This email is already used"));

    // WHEN
    mockMvc.perform(post("/register")
            .with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(jsonParam.toString()))

        // THEN
        .andExpect(status().isConflict())
        .andExpect(jsonPath("$", is("This email is already used")));
    verify(userService, times(1)).register(subscriptionCaptor.capture());
    UserDto expectedDto = new UserDto(0,"test","test", "existing@mail.com","password",null, null);
    assertThat(subscriptionCaptor.getValue()).usingRecursiveComparison().isEqualTo(expectedDto);
  }

  @DisplayName("GET login with successfully authentication should return 200 with user identity")
  @Test
  void loginTest() throws Exception {
    // WHEN
    mockMvc.perform(get("/login").with(user(userTest)))

        // THEN
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.userId", is(1)))
        .andExpect(jsonPath("$.role", is("USER")));
  }

  @DisplayName("GET login without successfully authentication should return 401")
  @Test
  void loginWithoutAuthenticationTest() throws Exception {
      // WHEN
    mockMvc.perform(get("/login"))

        // THEN
        .andExpect(status().isUnauthorized());
    verify(userService, times(0)).getById(anyInt());
  }

  @DisplayName("PUT setting user enabling should return 204")
  @Test
  void setAccountEnablingTest() throws Exception {
     // WHEN
    mockMvc.perform(put("/users/1/enable?value=false").with(user(adminTest)))

        // THEN
        .andExpect(status().isNoContent());
    verify(userService, times(1)).setAccountEnabling(1,false);
  }

  @DisplayName("PUT setting user enabling on non-existing user should return 404")
  @Test
  void setAccountEnablingWhenNotFoundTest() throws Exception {
    // GIVEN
    doThrow(new ResourceNotFoundException("This user is not found")).when(userService)
        .setAccountEnabling(anyInt(), anyBoolean());

    // WHEN
    mockMvc.perform(put("/users/9/enable?value=true").with(user(adminTest)))

        // THEN
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$", is("This user is not found")));
    verify(userService, times(1)).setAccountEnabling(9,true);
  }

  @DisplayName("PUT setting user enabling when not authenticate as admin should return 403")
  @Test
  void setAccountEnablingWhenNotAdminTest() throws Exception {
    // GIVEN

    // WHEN
    mockMvc.perform(put("/users/1/enable?value=false").with(user(userTest)))

        // THEN
        .andExpect(status().isForbidden());
    verify(userService, times(0)).setAccountEnabling(anyInt(),anyBoolean());
  }

}
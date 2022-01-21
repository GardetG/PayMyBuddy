package com.openclassrooms.paymybuddy.controller;

import static org.assertj.core.api.Assertions.assertThat;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.openclassrooms.paymybuddy.dto.UserDto;
import com.openclassrooms.paymybuddy.exception.ForbiddenOperationException;
import com.openclassrooms.paymybuddy.exception.ResourceAlreadyExistsException;
import com.openclassrooms.paymybuddy.exception.ResourceNotFoundException;
import com.openclassrooms.paymybuddy.model.Role;
import com.openclassrooms.paymybuddy.model.User;
import com.openclassrooms.paymybuddy.service.CredentialsService;
import com.openclassrooms.paymybuddy.service.UserService;
import java.math.BigDecimal;
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
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(value = UserController.class)
class UserControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private UserService userService;

  @MockBean
  private CredentialsService credentialsService;

  @Captor
  ArgumentCaptor<UserDto> infoCaptor;

  private UserDto userInfoDto;
  private User userTest;
  private User adminTest;
  private JSONObject jsonParam;

  @BeforeEach
  void setUp() {
    LocalDateTime date = LocalDateTime.of(2000,1,1,0,0);
    userInfoDto = new UserDto(1, "test","test","test@mail.com",null, BigDecimal.ZERO, date, true);
    userTest = new User("test","test","user1@mail.com","password", Role.USER, date);
    userTest.setUserId(1);
    adminTest = new User("test","test","test@mail.com","password", Role.ADMIN, date);
    jsonParam = new JSONObject();
  }

  @DisplayName("GET all user registered should return 200 with page of user Dto")
  @Test
  void getAllInfoTest() throws Exception {
    // GIVEN
    Pageable pageable = PageRequest.of(0,10);
    when(userService.getAll(any(Pageable.class))).thenReturn(new PageImpl<>(List.of(userInfoDto)));

    // WHEN
    mockMvc.perform(get("/users?page=0&size=10").with(user(adminTest)))

        // THEN
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content[0].userId", is(1)))
        .andExpect(jsonPath("$.totalPages", is(1)))
        .andExpect(jsonPath("$.totalElements", is(1)));
    verify(userService, times(1)).getAll(pageable);
  }

  @DisplayName("GET all user registered when not admin should return 403")
  @Test
  void getAllInfoWhenNotAdminTest() throws Exception {
    // WHEN
    mockMvc.perform(get("/users?page=0&size=10").with(user(userTest)))

        // THEN
        .andExpect(status().isForbidden());
  }

  @DisplayName("GET user info should return 200 with user Dto")
  @Test
  void getInfoByIdTest() throws Exception {
    // GIVEN
    when(userService.getById(anyInt())).thenReturn(userInfoDto);

    // WHEN
    mockMvc.perform(get("/users/1").with(user(userTest)))

        // THEN
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.userId", is(1)))
        .andExpect(jsonPath("$.firstname", is("test")))
        .andExpect(jsonPath("$.lastname", is("test")))
        .andExpect(jsonPath("$.email", is("test@mail.com")))
        .andExpect(jsonPath("$.password").doesNotExist())
        .andExpect(jsonPath("$.wallet", is(0)))
        .andExpect(jsonPath("$.registrationDate", is("2000-01-01 at 00:00")));
    verify(userService, times(1)).getById(1);
  }

  @DisplayName("GET user info from a non existent user should return 404")
  @Test
  void getInfoByIdWhenNotFoundTest() throws Exception {
    // GIVEN
    when(userService.getById(anyInt())).thenThrow(
        new ResourceNotFoundException("This user is not found"));

    // WHEN
    mockMvc.perform(get("/users/9").with(user(adminTest)))

        // THEN
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$", is("This user is not found")));
    verify(userService, times(1)).getById(9);
  }

  @DisplayName("GET user info from an other user should return 403")
  @Test
  void getInfoWhenAuthenticateButIdNotMatchingTest() throws Exception {
    // GIVEN
    when(userService.getById(anyInt())).thenReturn(userInfoDto);

    // WHEN
    mockMvc.perform(get("/users/2").with(user(userTest)))

        // THEN
        .andExpect(status().isForbidden());
    verify(userService, times(0)).getById(1);
  }

  @DisplayName("PUT user update should return 200 with user Dto")
  @Test
  void putUpdateTest() throws Exception {
    // GIVEN
    jsonParam.put("userId",1)
        .put("firstname","update")
        .put("lastname","test")
        .put("email","new@mail.com");
    LocalDateTime date = LocalDateTime.of(2000,1,1,0,0);
    UserDto updatedDto = new UserDto(1,"update", "test", "new@mail.com", null,BigDecimal.ZERO, date, true);
    when(userService.update(any(UserDto.class))).thenReturn(updatedDto);

    // WHEN
    mockMvc.perform(put("/users").with(user(userTest))
            .with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(jsonParam.toString()))

        // THEN
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.userId", is(1)))
        .andExpect(jsonPath("$.firstname", is("update")))
        .andExpect(jsonPath("$.lastname", is("test")))
        .andExpect(jsonPath("$.email", is("new@mail.com")))
        .andExpect(jsonPath("$.wallet", is(0)))
        .andExpect(jsonPath("$.registrationDate", is("2000-01-01 at 00:00")))
        .andExpect(jsonPath("$.password").doesNotExist());
    verify(userService, times(1)).update(infoCaptor.capture());
    UserDto expected = new UserDto(1,"update", "test", "new@mail.com", null,null,null, false);
    assertThat(infoCaptor.getValue()).usingRecursiveComparison().isEqualTo(expected);
  }

  @DisplayName("PUT invalid user update should return 422")
  @Test
  void putInvalidUpdateTest() throws Exception {
    // GIVEN
    jsonParam.put("userId",1)
        .put("firstname","")
        .put("  ","test")
        .put("email","mail.com")
        .put("password","123");

    // WHEN
    mockMvc.perform(put("/users").with(user(userTest))
            .with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(jsonParam.toString()))

        // THEN
        .andExpect(status().isUnprocessableEntity())
        .andExpect(jsonPath("$.firstname", is("Firstname is mandatory")))
        .andExpect(jsonPath("$.lastname", is("Lastname is mandatory")))
        .andExpect(jsonPath("$.email", is("Email should be a valid email address")))
        .andExpect(jsonPath("$.password", is("Password should have at least 8 characters")));
    verify(userService, times(0)).register(any(UserDto.class));
  }

  @DisplayName("PUT user update with already existing email should return 409")
  @Test
  void putUpdateWithAlreadyUsedEmailTest() throws Exception {
    // GIVEN
    jsonParam.put("userId",1)
        .put("firstname","update")
        .put("lastname","test")
        .put("email","existing@mail.com");
    when(userService.update(any(UserDto.class))).thenThrow(
        new ResourceAlreadyExistsException("This email is already used"));

    // WHEN
    mockMvc.perform(put("/users").with(user(userTest))
            .with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(jsonParam.toString()))

        // THEN
        .andExpect(status().isConflict())
        .andExpect(jsonPath("$", is("This email is already used")));
    verify(userService, times(1)).update(infoCaptor.capture());
    UserDto expected = new UserDto(1,"update", "test", "existing@mail.com", null, null,null, false);
    assertThat(infoCaptor.getValue()).usingRecursiveComparison().isEqualTo(expected);
  }

  @DisplayName("PUT user update of an other user should return 403")
  @Test
  void putUpdateWhenNotMatchingTest() throws Exception {
    // GIVEN
    jsonParam.put("userId",9).put("firstname","update")
        .put("lastname","test").put("email","update@mail.com");

    // WHEN
    mockMvc.perform(put("/users").with(user(userTest))
            .with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(jsonParam.toString()))

        // THEN
        .andExpect(status().isForbidden());
    verify(userService, times(0)).update(infoCaptor.capture());
  }

  @DisplayName("DELETE user should return 204")
  @Test
  void deleteUserTest() throws Exception {
    // WHEN
    mockMvc.perform(delete("/users/1").with(user(userTest)))

        // THEN
        .andExpect(status().isNoContent());
    verify(userService, times(1)).deleteById(1);
  }

  @DisplayName("DELETE non existent user should return 404")
  @Test
  void deleteUserWithUserNotFoundTest() throws Exception {
    // GIVEN
    doThrow(new ResourceNotFoundException("This user is not found")).when(userService)
        .deleteById(anyInt());

    // WHEN
    mockMvc.perform(delete("/users/9").with(user(adminTest)))

        // THEN
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$", is("This user is not found")));
    verify(userService, times(1)).deleteById(9);
  }

  @DisplayName("DELETE user with wallet not empty should return 409")
  @Test
  void deleteUserWithNotEmptyWalletTest() throws Exception {
    // GIVEN
    doThrow(new ForbiddenOperationException("Can't delete user if wallet not empty")).when(userService)
        .deleteById(anyInt());

    // WHEN
    mockMvc.perform(delete("/users/1").with(user(userTest)))

        // THEN
        .andExpect(status().isConflict())
        .andExpect(jsonPath("$", is("Can't delete user if wallet not empty")));
    verify(userService, times(1)).deleteById(1);
  }

  @DisplayName("DELETE an other user should return 403")
  @Test
  void deleteUserAuthenticateButIdNotMatchingTest() throws Exception {
    // WHEN
    mockMvc.perform(delete("/users/2").with(user(userTest)))

        // THEN
        .andExpect(status().isForbidden());
  }

}

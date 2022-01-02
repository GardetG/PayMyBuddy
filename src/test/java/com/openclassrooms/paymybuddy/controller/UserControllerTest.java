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
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.openclassrooms.paymybuddy.dto.UserInfoDto;
import com.openclassrooms.paymybuddy.dto.UserRegistrationDto;
import com.openclassrooms.paymybuddy.exception.EmailAlreadyExistsException;
import com.openclassrooms.paymybuddy.exception.ResourceNotFoundException;
import com.openclassrooms.paymybuddy.model.Role;
import com.openclassrooms.paymybuddy.model.User;
import com.openclassrooms.paymybuddy.service.CredentialsService;
import com.openclassrooms.paymybuddy.service.UserService;
import com.openclassrooms.paymybuddy.utils.JsonParser;
import java.math.BigDecimal;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
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
  ArgumentCaptor<UserRegistrationDto> subscriptionCaptor;
  @Captor
  ArgumentCaptor<UserInfoDto> infoCaptor;

  private UserInfoDto userInfoDto;
  private User userTest;
  private User adminTest;

  @BeforeEach
  void setUp() {
    userInfoDto = new UserInfoDto(1, "test","test","test@mail.com", BigDecimal.ZERO, "USER");
    userTest = new User(1,"test","test","test@mail.com","password",BigDecimal.ZERO, new Role(0,"USER"));
    adminTest = new User(1,"test","test","test@mail.com","password",BigDecimal.ZERO, new Role(0,"ADMIN"));
  }

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

  @Test
  void getAllInfoWhenNotAdminTest() throws Exception {
    // GIVEN

    // WHEN
    mockMvc.perform(get("/users?page=0&size=10").with(user(userTest)))

        // THEN
        .andExpect(status().isForbidden());
  }

  @Test
  void getInfoByIdTest() throws Exception {
    // GIVEN
    when(userService.getInfoById(anyInt())).thenReturn(userInfoDto);

    // WHEN
    mockMvc.perform(get("/users/1").with(user(userTest)))

        // THEN
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.userId", is(1)))
        .andExpect(jsonPath("$.firstname", is("test")))
        .andExpect(jsonPath("$.lastname", is("test")))
        .andExpect(jsonPath("$.email", is("test@mail.com")))
        .andExpect(jsonPath("$.wallet", is(0)));
    verify(userService, times(1)).getInfoById(1);
  }

  @Test
  void getInfoByIdWhenNotFoundTest() throws Exception {
    // GIVEN
    when(userService.getInfoById(anyInt())).thenThrow(
        new ResourceNotFoundException("This user is not found"));

    // WHEN
    mockMvc.perform(get("/users/2").with(user(userTest)))

        // THEN
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$", is("This user is not found")));
    verify(userService, times(1)).getInfoById(2);
  }

  @Test
  void getInfoWhenNotAuthenticateTest() throws Exception {
    // GIVEN
    when(userService.getInfoById(anyInt())).thenReturn(userInfoDto);

    // WHEN
    mockMvc.perform(get("/users/1"))

        // THEN
        .andExpect(status().isUnauthorized());
    verify(userService, times(0)).getInfoById(1);
  }

  @Test
  void postSubscriptionTest() throws Exception {
    // GIVEN
    UserRegistrationDto
        subscriptionDto = new UserRegistrationDto("test","test", "test@mail.com","12345678");
    when(userService.register(any(UserRegistrationDto.class))).thenReturn(userInfoDto);

    // WHEN
    mockMvc.perform(post("/register")
            .with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(JsonParser.asString(subscriptionDto)))

        // THEN
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.userId", is(1)))
        .andExpect(jsonPath("$.firstname", is("test")))
        .andExpect(jsonPath("$.lastname", is("test")))
        .andExpect(jsonPath("$.email", is("test@mail.com")))
        .andExpect(jsonPath("$.wallet", is(0)));
    verify(userService, times(1)).register(subscriptionCaptor.capture());
    assertThat(subscriptionCaptor.getValue()).usingRecursiveComparison().isEqualTo(subscriptionDto);

  }

  @Test
  void postSubscriptionWithAlreadyUsedEmailTest() throws Exception {
    // GIVEN
    UserRegistrationDto
        subscriptionDto = new UserRegistrationDto("test","test", "test@mail.com","12345678");
    when(userService.register(any(UserRegistrationDto.class))).thenThrow(
        new EmailAlreadyExistsException("This email is already used"));

    // WHEN
    mockMvc.perform(post("/register")
            .with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(JsonParser.asString(subscriptionDto)))

        // THEN
        .andExpect(status().isConflict())
        .andExpect(jsonPath("$", is("This email is already used")));
    verify(userService, times(1)).register(subscriptionCaptor.capture());
    assertThat(subscriptionCaptor.getValue()).usingRecursiveComparison().isEqualTo(subscriptionDto);
  }

  @Test
  void postInvalidSubscriptionTest() throws Exception {
    // GIVEN
    UserRegistrationDto
        invalidSubscriptionDto = new UserRegistrationDto("","test", "testmail.com","1234");

    // WHEN
    mockMvc.perform(post("/register")
            .with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(JsonParser.asString(invalidSubscriptionDto)))

        // THEN
        .andExpect(status().isUnprocessableEntity())
        .andExpect(jsonPath("$.firstname", is("Firstname is mandatory")))
        .andExpect(jsonPath("$.email", is("Email should be a valid email address")))
        .andExpect(jsonPath("$.password", is("Password should have at least 8 characters")));
    verify(userService, times(0)).register(any(UserRegistrationDto.class));
  }

  @Test
  void putUpdateTest() throws Exception {
    // GIVEN
    UserInfoDto updateDto = new UserInfoDto(1,"update", "test", "update@mail.com", BigDecimal.ZERO, "USER");
    when(userService.update(any(UserInfoDto.class))).thenReturn(updateDto);

    // WHEN
    mockMvc.perform(put("/users").with(user(userTest))
            .with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(JsonParser.asString(updateDto)))

        // THEN
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.userId", is(1)))
        .andExpect(jsonPath("$.firstname", is("update")))
        .andExpect(jsonPath("$.lastname", is("test")))
        .andExpect(jsonPath("$.email", is("update@mail.com")))
        .andExpect(jsonPath("$.wallet", is(0)));
    verify(userService, times(1)).update(infoCaptor.capture());
    assertThat(infoCaptor.getValue()).usingRecursiveComparison().isEqualTo(updateDto);

  }

  @Test
  void putUpdateWithAlreadyUsedEmailTest() throws Exception {
    // GIVEN
    UserInfoDto updateDto = new UserInfoDto(1,"update", "test", "existing@mail.com", BigDecimal.ZERO, "USER");
    when(userService.update(any(UserInfoDto.class))).thenThrow(
        new EmailAlreadyExistsException("This email is already used"));

    // WHEN
    mockMvc.perform(put("/users").with(user(userTest))
            .with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(JsonParser.asString(updateDto)))

        // THEN
        .andExpect(status().isConflict())
        .andExpect(jsonPath("$", is("This email is already used")));
    verify(userService, times(1)).update(infoCaptor.capture());
    assertThat(infoCaptor.getValue()).usingRecursiveComparison().isEqualTo(updateDto);
  }

  @Test
  void putUpdateWhenNotFoundTest() throws Exception {
    // GIVEN
    UserInfoDto updateDto = new UserInfoDto(2,"update", "test", "update@mail.com", BigDecimal.ZERO, "USER");
    when(userService.update(any(UserInfoDto.class))).thenThrow(
        new ResourceNotFoundException("This user is not found"));

    // WHEN
    mockMvc.perform(put("/users").with(user(userTest))
            .with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(JsonParser.asString(updateDto)))

        // THEN
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$", is("This user is not found")));
    verify(userService, times(1)).update(infoCaptor.capture());
    assertThat(infoCaptor.getValue()).usingRecursiveComparison().isEqualTo(updateDto);
  }

  @Test
  void putInvalidUpdateTest() throws Exception {
    // GIVEN
    UserInfoDto invalidUpdateDto = new UserInfoDto(1,"","test", "testmail.com", BigDecimal.ZERO, "USER");

    // WHEN
    mockMvc.perform(put("/users").with(user(userTest))
            .with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(JsonParser.asString(invalidUpdateDto)))

        // THEN
        .andExpect(status().isUnprocessableEntity())
        .andExpect(jsonPath("$.firstname", is("Firstname is mandatory")))
        .andExpect(jsonPath("$.email", is("Email should be a valid email address")));
    verify(userService, times(0)).register(any(UserRegistrationDto.class));
  }

  @Test
  void putUpdateWhenNotAuthenticateTest() throws Exception {
    // GIVEN
    UserInfoDto updateDto = new UserInfoDto(1,"update", "test", "update@mail.com", BigDecimal.ZERO, "USER");

    // WHEN
    mockMvc.perform(put("/users")
            .with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(JsonParser.asString(updateDto)))

        // THEN
        .andExpect(status().isUnauthorized());
  }

  @Test
  void deleteUserTest() throws Exception {
    // GIVEN

    // WHEN
    mockMvc.perform(delete("/users/1").with(user(userTest)))

        // THEN
        .andExpect(status().isNoContent());
    verify(userService, times(1)).deleteById(1);
  }

  @Test
  void deleteUserWhenNotFoundTest() throws Exception {
    // GIVEN
    doThrow(new ResourceNotFoundException("This user is not found")).when(userService).deleteById(anyInt());

    // WHEN
    mockMvc.perform(delete("/users/2").with(user(userTest)))

        // THEN
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$", is("This user is not found")));
    verify(userService, times(1)).deleteById(2);
  }

  @Test
  void deleteUserWhenNotAuthenticateTest() throws Exception {
    // GIVEN

    // WHEN
    mockMvc.perform(delete("/users/1"))

        // THEN
        .andExpect(status().isUnauthorized());
  }
}

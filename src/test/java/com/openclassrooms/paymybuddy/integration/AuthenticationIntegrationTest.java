package com.openclassrooms.paymybuddy.integration;

import static org.hamcrest.CoreMatchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.jayway.jsonpath.JsonPath;
import com.openclassrooms.paymybuddy.integration.utils.CredentialUtils;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
class AuthenticationIntegrationTest {

  @Autowired
  private MockMvc mockMvc;

  private JSONObject jsonParam;

  @BeforeEach
  void setUp() {
    jsonParam = new JSONObject();
  }

  @DisplayName("Register a new user and successfully login")
  @Test
  void registerTest() throws Exception {
    // GIVEN
    jsonParam.put("firstname","test")
        .put("lastname","test")
        .put("email","test@mail.com")
        .put("password","12345678");

    // WHEN
    MvcResult result = mockMvc.perform(post("/register")
            .contentType(MediaType.APPLICATION_JSON)
            .content(jsonParam.toString()))
    // THEN registered successfully
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.firstname", is("test")))
        .andExpect(jsonPath("$.lastname", is("test")))
        .andExpect(jsonPath("$.email", is("test@mail.com")))
        .andExpect(jsonPath("$.wallet", is(0)))
        .andExpect(jsonPath("$.registrationDate").exists())
        .andExpect(jsonPath("$.password").doesNotExist())
        .andReturn();
    int id = JsonPath.read(result.getResponse().getContentAsString(), "$.userId");

    // WHEN
    mockMvc.perform(get("/login")
            .header(HttpHeaders.AUTHORIZATION, CredentialUtils.encode("test@mail.com","12345678")))
    // THEN login successfully
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.userId", is(id)))
        .andExpect(jsonPath("$.role", is("USER")));
  }

  @DisplayName("Disable an account by admin and failed login")
  @Test
  void AdminDisableAccountTest() throws Exception {
    // WHEN
    mockMvc.perform(get("/users?page=0&size=10&sort=userId,asc")
            .header(HttpHeaders.AUTHORIZATION, CredentialUtils.encode("admin@mail.com","password")))
    // THEN user 2 exist and enabled
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content[1].userId", is(2)))
        .andExpect(jsonPath("$.content[1].enabled", is(true)));

    // WHEN
    mockMvc.perform(put("/users/2/enable?value=false")
            .header(HttpHeaders.AUTHORIZATION, CredentialUtils.encode("admin@mail.com","password")))
    // THEN user 2 account successfully disabled
        .andExpect(status().isNoContent());

    // WHEN
    mockMvc.perform(get("/login")
            .header(HttpHeaders.AUTHORIZATION, CredentialUtils.encode("user@mail.com","password")))
    // THEN login failed
        .andExpect(status().isUnauthorized());
  }

}

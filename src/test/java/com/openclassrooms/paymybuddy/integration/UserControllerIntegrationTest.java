package com.openclassrooms.paymybuddy.integration;

import static org.hamcrest.CoreMatchers.is;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.jayway.jsonpath.JsonPath;
import com.openclassrooms.paymybuddy.dto.UserInfoDto;
import com.openclassrooms.paymybuddy.dto.UserRegistrationDto;
import com.openclassrooms.paymybuddy.utils.JsonParser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.Base64Utils;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest
@AutoConfigureMockMvc
class UserControllerIntegrationTest {

  @Autowired
  private WebApplicationContext context;

  @Autowired
  private MockMvc mockMvc;

  private String encodeCredentials(String username, String password) {
    String credentials = String.format("%s:%s", username, password);
    return "Basic " + Base64Utils.encodeToString(credentials.getBytes());
  }

  @Test
  void subscribeIntegrationTest() throws Exception {
    // GIVEN
    UserRegistrationDto
        subscriptionDto = new UserRegistrationDto("test", "test", "test@mail.com", "12345678");

    // WHEN
    MvcResult result = mockMvc.perform(post("/register")
            .contentType(MediaType.APPLICATION_JSON)
            .content(JsonParser.asString(subscriptionDto)))

        // THEN
        // Check response
        .andExpect(status().isCreated()).andReturn();

    // Check that user successfully registered
    int id = JsonPath.read(result.getResponse().getContentAsString(), "$.userId");
    mockMvc.perform(get("/users/" + id)
            .header(HttpHeaders.AUTHORIZATION,encodeCredentials("test@mail.com","12345678")))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.firstname", is("test")))
        .andExpect(jsonPath("$.lastname", is("test")))
        .andExpect(jsonPath("$.email", is("test@mail.com")))
        .andExpect(jsonPath("$.wallet", is(0)));
  }

  @Test
  void updateTest() throws Exception {
    // GIVEN
    UserInfoDto updatedUser = new UserInfoDto(3, "update", "test", "update@mail.com", null, "USER");

    // WHEN
    mockMvc.perform(put("/users")
            .header(HttpHeaders.AUTHORIZATION,encodeCredentials("user2@mail.com","password"))
            .contentType(MediaType.APPLICATION_JSON)
            .content(JsonParser.asString(updatedUser)))

        // THEN
        // Check response
        .andExpect(status().isOk());
    // Check that the user successfully updated
    mockMvc.perform(get("/users/" + updatedUser.getUserId())
            .header(HttpHeaders.AUTHORIZATION,encodeCredentials("update@mail.com","password")))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.firstname", is("update")))
        .andExpect(jsonPath("$.lastname", is("test")))
        .andExpect(jsonPath("$.email", is("update@mail.com")))
        .andExpect(jsonPath("$.wallet", is(100)));
  }

  @Test
  void deleteIntegrationTest() throws Exception {
    // GIVEN
    int id = 2;

    // WHEN
    mockMvc.perform(delete("/users/" + id)
        .header(HttpHeaders.AUTHORIZATION,encodeCredentials("user@mail.com","password")))
        // THEN
        // Check response
        .andExpect(status().isNoContent());
    // Check that the user is deleted
    mockMvc.perform(get("/users/" + id)
        .header(HttpHeaders.AUTHORIZATION,encodeCredentials("user@mail.com","password")))
        .andExpect(status().isUnauthorized());
  }
}

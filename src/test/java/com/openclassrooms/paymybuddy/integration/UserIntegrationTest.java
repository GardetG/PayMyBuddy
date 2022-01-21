package com.openclassrooms.paymybuddy.integration;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
class UserIntegrationTest {

  @Autowired
  private MockMvc mockMvc;

  private JSONObject jsonParam;

  @BeforeEach
  void setUp() {
    jsonParam = new JSONObject();
  }

  @DisplayName("Update an user firstname and lastname and check profile")
  @Test
  void updateNamesTest() throws Exception {
    // GIVEN
    jsonParam.put("userId",2)
        .put("firstname","update")
        .put("lastname","update")
        .put("email","user1@mail.com");

    // WHEN
    mockMvc.perform(put("/users")
            .header(HttpHeaders.AUTHORIZATION, CredentialUtils.encode("user1@mail.com","password"))
            .contentType(MediaType.APPLICATION_JSON)
            .content(jsonParam.toString()))
    // THEN User updated
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.firstname", is("update")))
        .andExpect(jsonPath("$.lastname", is("update")));

    // WHEN
    mockMvc.perform(get("/users/2")
            .header(HttpHeaders.AUTHORIZATION, CredentialUtils.encode("user1@mail.com","password")))
    // THEN Getting updated user profile
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.firstname", is("update")))
        .andExpect(jsonPath("$.lastname", is("update")))
        .andExpect(jsonPath("$.email", is("user1@mail.com")))
        .andExpect(jsonPath("$.wallet", is(100.0)))
        .andExpect(jsonPath("$.registrationDate", is("2000-01-01 at 00:00")))
        .andExpect(jsonPath("$.password").doesNotExist());
  }

  @DisplayName("Update an user credentials and successfully login")
  @Test
  void updateEmailAndPasswordTest() throws Exception {
    // GIVEN
    jsonParam.put("userId",2)
        .put("firstname","User1")
        .put("lastname","test")
        .put("email","update@mail.com")
        .put("password","newpassword");

    // WHEN
    mockMvc.perform(put("/users")
            .header(HttpHeaders.AUTHORIZATION, CredentialUtils.encode("user1@mail.com","password"))
            .contentType(MediaType.APPLICATION_JSON)
            .content(jsonParam.toString()))
    // THEN Updated successfully
        .andExpect(status().isOk());

    // WHEN
    mockMvc.perform(get("/login")
            .header(HttpHeaders.AUTHORIZATION, CredentialUtils.encode("update@mail.com","newpassword")))
    // THEN Login successfully with new credentials
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.userId", is(2)))
        .andExpect(jsonPath("$.role", is("USER")));
  }

  @DisplayName("Delete a user with connections, bank account and involved in transaction")
  @Test
  void deleteIntegrationTest() throws Exception {
    // WHEN
    mockMvc.perform(delete("/users/3")
        .header(HttpHeaders.AUTHORIZATION,CredentialUtils.encode("user2@mail.com","password")))

    // THEN delete user 3 successfully
        .andExpect(status().isNoContent());

    // WHEN
    mockMvc.perform(get("/users/3")
        .header(HttpHeaders.AUTHORIZATION,CredentialUtils.encode("admin@mail.com","password")))
    // THEN user 2 doesn't exist anymore
        .andExpect(status().isNotFound());

    // WHEN
    mockMvc.perform(get("/banktransfers")
            .header(HttpHeaders.AUTHORIZATION,CredentialUtils.encode("admin@mail.com","password")))
    // THEN Bank transfer involving user 2 has been cleared
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content", hasSize(0)));

    // WHEN
    mockMvc.perform(get("/transactions")
            .header(HttpHeaders.AUTHORIZATION,CredentialUtils.encode("admin@mail.com","password")))
    // THEN transaction involving user 3 has been cleaned
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content", hasSize(1)))
        .andExpect(jsonPath("$.content[0].emitterId", is(0)));
  }
}

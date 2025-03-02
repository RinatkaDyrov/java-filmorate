package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldCreateAndUpdateUser() throws Exception {
        String userJson = "{" +
                "\"name\": \"Gandalf\"," +
                "\"login\": \"GandalfTheGrey\"," +
                "\"email\": \"wizard@common.ru\"," +
                "\"birthday\": \"2000-08-20\"" +
                "}";

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Gandalf"))
                .andExpect(jsonPath("$.login").value("GandalfTheGrey"))
                .andExpect(jsonPath("$.email").value("wizard@common.ru"));

        String updUserJson = "{" +
                "\"id\": 1," +
                "\"name\": \"Gandalf\"," +
                "\"login\": \"GandalfTheWhite\"," +
                "\"email\": \"greatWizard@common.ru\"," +
                "\"birthday\": \"2000-08-20\"" +
                "}";

        mockMvc.perform(put("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updUserJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.login").value("GandalfTheWhite"))
                .andExpect(jsonPath("$.email").value("greatWizard@common.ru"));
    }

    @Test
    void shouldNotCreateUserWithInvalidLogin() throws Exception {
        String invalidUserJson = "{" +
                "\"login\": \"invalid login\"," +
                "\"email\": \"test@mail.com\"," +
                "\"birthday\": \"1990-01-01\"" +
                "}";
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidUserJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldNotCreateUserWithInvalidEmail() throws Exception {
        String invalidUserJson = "{" +
                "\"login\": \"login\"," +
                "\"email\": \"invalidmail.com\"," +
                "\"birthday\": \"1990-01-01\"" +
                "}";
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidUserJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldNotCreateUserWithBirthdateInFuture() throws Exception {
        String invalidUserJson = "{" +
                "\"name\": \"Garfield\"," +
                "\"login\": \"common\"," +
                "\"email\": \"friend@common.ru\"," +
                "\"birthday\": \"2030-08-20\"" +
                "}";
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidUserJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldCreateUserWithoutName() throws Exception {
        String userJson = "{" +
                "\"login\": \"NamelessDude\"," +
                "\"email\": \"dude@mail.com\"," +
                "\"birthday\": \"1890-01-01\"" +
                "}";
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("NamelessDude"))
                .andExpect(jsonPath("$.login").value("NamelessDude"))
                .andExpect(jsonPath("$.email").value("dude@mail.com"));
    }
}
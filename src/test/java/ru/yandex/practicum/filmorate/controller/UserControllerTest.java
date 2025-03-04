package ru.yandex.practicum.filmorate.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    @MockBean
    private UserStorage userStorage;

    private User testUser1;
    private User testUser2;

    @BeforeEach
    void setUp() {
        testUser1 = new User();
        testUser1.setId(1L);
        testUser1.setEmail("john@example.com");
        testUser1.setLogin("john_doe");
        testUser1.setName("John Doe");
        testUser1.setBirthday(LocalDate.of(1990, 1, 1));

        testUser2 = new User();
        testUser2.setId(2L);
        testUser2.setEmail("jane@example.com");
        testUser2.setLogin("jane_doe");
        testUser2.setName("Jane Doe");
        testUser2.setBirthday(LocalDate.of(1992, 5, 15));
    }

    @Test
    void shouldReturnAllUsers() throws Exception {
        when(userService.getAllUsers()).thenReturn(List.of(testUser1, testUser2));

        mockMvc.perform(get("/users"))
                .andDo(print()) // 👈 Добавлено для вывода ответа в консоль
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[1].id", is(2)));
    }

    @Test
    void shouldCreateUser() throws Exception {
        when(userService.createUser(any(User.class))).thenReturn(testUser1);

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testUser1)))
                .andDo(print()) // 👈 Добавлено для вывода ответа в консоль
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("John Doe")))
                .andExpect(jsonPath("$.email", is("john@example.com")));
    }

    @Test
    void shouldUpdateUser() throws Exception {
        testUser1.setName("Updated Name");

        when(userService.updateUser(any(User.class))).thenReturn(testUser1);

        mockMvc.perform(put("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testUser1)))
                .andDo(print()) // 👈 Добавлено для вывода ответа в консоль
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("Updated Name")));
    }

    @Test
    void shouldAddFriend() throws Exception {
        mockMvc.perform(put("/users/1/friends/2"))
                .andDo(print()) // 👈 Добавлено для вывода ответа в консоль
                .andExpect(status().isOk());
    }

    @Test
    void shouldDeleteFriend() throws Exception {
        mockMvc.perform(delete("/users/1/friends/2"))
                .andDo(print()) // 👈 Добавлено для вывода ответа в консоль
                .andExpect(status().isOk());
    }

    @Test
    void shouldGetUserFriends() throws Exception {
        when(userService.getFriendByUserId(1L)).thenReturn(List.of(testUser2));

        mockMvc.perform(get("/users/1/friends"))
                .andDo(print()) // 👈 Добавлено для вывода ответа в консоль
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(2)));
    }

    @Test
    void shouldGetCommonFriends() throws Exception {
        when(userService.getCommonFriends(1L, 2L)).thenReturn(List.of(testUser2));

        mockMvc.perform(get("/users/1/friends/common/2"))
                .andDo(print()) // 👈 Добавлено для вывода ответа в консоль
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(2)));
    }

    @Test
    void shouldReturn404IfUserNotFound() throws Exception {
        when(userService.getFriendByUserId(99L)).thenThrow(new NotFoundException("User not found"));

        mockMvc.perform(get("/users/99/friends"))
                .andDo(print()) // 👈 Добавлено для вывода ответа в консоль
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error", is("not found error")))
                .andExpect(jsonPath("$.description", is("User not found")));
    }
}

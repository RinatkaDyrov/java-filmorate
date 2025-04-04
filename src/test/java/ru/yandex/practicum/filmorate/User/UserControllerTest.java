package ru.yandex.practicum.filmorate.User;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.dto.user.NewUserRequest;
import ru.yandex.practicum.filmorate.dto.user.UserDto;
import ru.yandex.practicum.filmorate.service.UserService;

import java.util.Arrays;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
    }

    @Test
    void findAllShouldReturnUserList() throws Exception {
        UserDto user1 = new UserDto();
        user1.setId(1L);
        user1.setName("user1");
        user1.setEmail("user1@example.com");
        UserDto user2 = new UserDto();
        user2.setId(2L);
        user2.setName("user2");
        user2.setEmail("user2@example.com");

        when(userService.getAllUsers()).thenReturn(Arrays.asList(user1, user2));

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk()) // Проверяем статус 200
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void createShouldReturnCreatedUser() throws Exception {
        NewUserRequest newUserRequest = new NewUserRequest();
        newUserRequest.setEmail("newUser@example.com");
        newUserRequest.setLogin("newUserLogin");

        UserDto userDto = new UserDto();
        userDto.setId(1L);
        userDto.setEmail("newUser@example.com");
        userDto.setLogin("newUserLogin");

        when(userService.createUser(any(NewUserRequest.class))).thenReturn(userDto);
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(newUserRequest)))
                .andExpect(status().isCreated()) // Проверяем, что статус 201
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.email").value("newUser@example.com"))
                .andExpect(jsonPath("$.login").value("newUserLogin"));
    }

    @Test
    void addFriendShouldReturnOk() throws Exception {
        doNothing().when(userService).addFriend(1L, 2L);

        mockMvc.perform(put("/users/1/friends/2"))
                .andExpect(status().isOk());
    }

    @Test
    void deleteFriendShouldReturnOk() throws Exception {
        doNothing().when(userService).deleteFriend(1L, 2L);

        mockMvc.perform(delete("/users/1/friends/2"))
                .andExpect(status().isOk());
    }

    @Test
    void getFriendsByUserIdShouldReturnFriends() throws Exception {
        UserDto user1 = new UserDto();
        user1.setId(1L);
        user1.setName("user1");
        user1.setEmail("user1@example.com");
        UserDto user2 = new UserDto();
        user2.setId(2L);
        user2.setName("user2");
        user2.setEmail("user2@example.com");

        when(userService.getFriendByUserId(1L)).thenReturn(Arrays.asList(user1, user2));

        mockMvc.perform(get("/users/1/friends"))
                .andExpect(status().isOk()) // Проверяем статус 200
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void getCommonFriendsShouldReturnCommonFriends() throws Exception {
        UserDto user1 = new UserDto();
        user1.setId(1L);
        user1.setName("user1");
        user1.setEmail("user1@example.com");
        UserDto user2 = new UserDto();
        user2.setId(2L);
        user2.setName("user2");
        user2.setEmail("user2@example.com");

        when(userService.getCommonFriends(1L, 2L)).thenReturn(Arrays.asList(user1, user2));

        mockMvc.perform(get("/users/1/friends/common/2"))
                .andExpect(status().isOk()) // Проверяем статус 200
                .andExpect(jsonPath("$.length()").value(2));
    }
}

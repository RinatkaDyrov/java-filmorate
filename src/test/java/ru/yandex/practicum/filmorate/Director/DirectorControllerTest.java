package ru.yandex.practicum.filmorate.Director;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.yandex.practicum.filmorate.controller.DirectorController;
import ru.yandex.practicum.filmorate.dto.director.DirectorDto;
import ru.yandex.practicum.filmorate.dto.director.NewDirectorRequest;
import ru.yandex.practicum.filmorate.dto.director.UpdateDirectorRequest;
import ru.yandex.practicum.filmorate.service.DirectorService;

import java.util.Collections;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class DirectorControllerTest {

    private MockMvc mockMvc;

    @Mock
    private DirectorService directorService;

    @InjectMocks
    private DirectorController directorController;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(directorController).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    void findAllShouldReturnDirectors() throws Exception {
        DirectorDto director = new DirectorDto();
        director.setId(1L);
        director.setName("Тарантино");

        when(directorService.getAllDirectors()).thenReturn(Collections.singletonList(director));

        mockMvc.perform(get("/directors"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].name").value("Тарантино"));
    }

    @Test
    void findByIdShouldReturnDirector() throws Exception {
        DirectorDto director = new DirectorDto();
        director.setId(1L);
        director.setName("Нолан");

        when(directorService.getDirectorById(1L)).thenReturn(director);

        mockMvc.perform(get("/directors/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Нолан"));
    }

    @Test
    void createShouldReturnCreatedDirector() throws Exception {
        NewDirectorRequest request = new NewDirectorRequest();
        request.setName("Кубрик");

        DirectorDto created = new DirectorDto();
        created.setId(1L);
        created.setName("Кубрик");

        when(directorService.createDirector(any(NewDirectorRequest.class))).thenReturn(created);

        mockMvc.perform(post("/directors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Кубрик"));
    }

    @Test
    void updateShouldReturnUpdatedDirector() throws Exception {
        UpdateDirectorRequest request = new UpdateDirectorRequest();
        request.setId(1L);
        request.setName("Ридли Скотт");

        DirectorDto updated = new DirectorDto();
        updated.setId(1L);
        updated.setName("Ридли Скотт");

        when(directorService.updateDirector(any(UpdateDirectorRequest.class))).thenReturn(updated);

        mockMvc.perform(put("/directors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Ридли Скотт"));
    }

    @Test
    void deleteShouldReturnOk() throws Exception {
        doNothing().when(directorService).deleteDirectorById(1L);

        mockMvc.perform(delete("/directors/1"))
                .andExpect(status().isOk());
    }
}
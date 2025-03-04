package ru.yandex.practicum.filmorate.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.time.LocalDate;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(FilmController.class)
class FilmControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private FilmService filmService;

    @MockBean
    private FilmStorage filmStorage;

    private Film testFilm1;
    private Film testFilm2;

    @BeforeEach
    void setUp() {
        testFilm1 = new Film();
        testFilm1.setId(1L);
        testFilm1.setName("Inception");
        testFilm1.setDescription("A mind-bending thriller");
        testFilm1.setReleaseDate(LocalDate.of(2010, 7, 16));
        testFilm1.setDuration(148);

        testFilm2 = new Film();
        testFilm2.setId(2L);
        testFilm2.setName("The Matrix");
        testFilm2.setDescription("A sci-fi classic");
        testFilm2.setReleaseDate(LocalDate.of(1999, 3, 31));
        testFilm2.setDuration(136);
    }

    @Test
    void shouldReturnAllFilms() throws Exception {
        when(filmService.findAllFilms()).thenReturn(List.of(testFilm1, testFilm2));

        mockMvc.perform(get("/films"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[1].id", is(2)));
    }

    @Test
    void shouldCreateFilm() throws Exception {
        when(filmService.createFilm(any(Film.class))).thenReturn(testFilm1);

        mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testFilm1)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Inception")))
                .andExpect(jsonPath("$.description", is("A mind-bending thriller")));
    }

    @Test
    void shouldUpdateFilm() throws Exception {
        testFilm1.setName("Inception - Director's Cut");

        when(filmService.updateFilm(any(Film.class))).thenReturn(testFilm1);

        mockMvc.perform(put("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testFilm1)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("Inception - Director's Cut")));
    }

    @Test
    void shouldLikeFilm() throws Exception {
        mockMvc.perform(put("/films/1/like/2"))
                .andExpect(status().isOk());
    }

    @Test
    void shouldDeleteLike() throws Exception {
        mockMvc.perform(delete("/films/1/like/2"))
                .andExpect(status().isOk());
    }

    @Test
    void shouldGetPopularFilms() throws Exception {
        when(filmService.getPopularFilms(10)).thenReturn(List.of(testFilm1, testFilm2));

        mockMvc.perform(get("/films/popular?count=10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[1].id", is(2)));
    }

    @Test
    void shouldReturn404IfFilmNotFound() throws Exception {
        when(filmService.findAllFilms()).thenReturn(List.of());

        mockMvc.perform(get("/films/99"))
                .andExpect(status().isNotFound());
    }
}

package ru.yandex.practicum.filmorate.Film;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.dto.film.FilmDto;
import ru.yandex.practicum.filmorate.dto.film.NewFilmRequest;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class FilmControllerTest {

    private MockMvc mockMvc;

    @InjectMocks
    private FilmController filmController;

    @Mock
    private FilmService filmService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(filmController).build();
    }

    @Test
    public void addFilmValidFilmReturnsCreated() throws Exception {
        NewFilmRequest filmRequest = new NewFilmRequest();
        filmRequest.setName("Valid Film");
        filmRequest.setDescription("This is a valid description.");
        filmRequest.setReleaseDate(LocalDate.now());
        filmRequest.setDuration(120);

        FilmDto filmDto = new FilmDto();
        filmDto.setId(1L);
        filmDto.setName("Valid Film");
        filmDto.setDescription("This is a valid description.");
        filmDto.setReleaseDate(LocalDate.now());
        filmDto.setDuration(120);

        when(filmService.createFilm(filmRequest)).thenReturn(filmDto);

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        String filmJson = objectMapper.writeValueAsString(filmRequest);

        mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(filmJson))
                .andExpect(status().isCreated());
    }

    @Test
    public void addFilmInvalidDurationReturnsBadRequest() throws Exception {
        FilmDto film = new FilmDto();
        film.setId(1L);
        film.setName("Invalid Duration Film");
        film.setDescription("This film has invalid duration.");
        film.setReleaseDate(LocalDate.now());
        film.setDuration(-10);

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        String filmJson = objectMapper.writeValueAsString(film);

        mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(filmJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void getFilmsReturnsFilmList() throws Exception {
        FilmDto film1 = new FilmDto();
        film1.setId(1L);
        film1.setName("Film 1");
        film1.setDescription("Description 1");

        FilmDto film2 = new FilmDto();
        film2.setId(2L);
        film2.setName("Film 2");
        film2.setDescription("Description 2");

        List<FilmDto> films = Arrays.asList(film1, film2);
        when(filmService.getAllFilms()).thenReturn(films);

        mockMvc.perform(get("/films")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Film 1"))
                .andExpect(jsonPath("$[1].name").value("Film 2"));
    }

    @Test
    public void getFilmByIdReturnsFilm() throws Exception {
        FilmDto film = new FilmDto();
        film.setId(1L);
        film.setName("Film 1");
        film.setDescription("Description 1");

        when(filmService.findFilmById(1)).thenReturn(film);

        mockMvc.perform(get("/films/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Film 1"));
    }

    @Test
    public void addLikeValidFilmAndUserReturnsNoContent() throws Exception {
        int filmId = 1;
        int userId = 1;

        doNothing().when(filmService).setLike(filmId, userId);

        mockMvc.perform(put("/films/{filmId}/like/{userId}", filmId, userId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void removeLikeValidFilmAndUserReturnsNoContent() throws Exception {
        int filmId = 1;
        int userId = 1;

        doNothing().when(filmService).deleteLike(filmId, userId);

        mockMvc.perform(delete("/films/{filmId}/like/{userId}", filmId, userId))
                .andExpect(status().isOk());

        verify(filmService, times(1)).deleteLike(filmId, userId);
    }
}

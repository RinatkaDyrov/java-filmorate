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
public class FilmControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldCreateAndUpdateFilm() throws Exception {
        String filmJson = """
                {
                  "name": "Lord of the Ring",
                  "description": "The Fellowship of the Ring",
                  "releaseDate": "2001-12-10",
                  "duration": 178
                }
                """;

        mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(filmJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Lord of the Ring"))
                .andExpect(jsonPath("$.description").value("The Fellowship of the Ring"))
                .andExpect(jsonPath("$.releaseDate").value("2001-12-10"))
                .andExpect(jsonPath("$.duration").value("178"));

        String updUserJson = """
                {
                    "id": 1,
                    "name": "Lord of the Ring",
                    "description": "Two towers",
                    "releaseDate": "2002-12-05",
                    "duration": 179
                }
                """;

        mockMvc.perform(put("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updUserJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Lord of the Ring"))
                .andExpect(jsonPath("$.description").value("Two towers"))
                .andExpect(jsonPath("$.releaseDate").value("2002-12-05"))
                .andExpect(jsonPath("$.duration").value("179"));
    }

    @Test
    void shouldNotCreateFilmWithInvalidName() throws Exception {
        String invalidFilmJson = """
                {
                  "description": "The Fellowship of the Ring",
                  "releaseDate": "2001-12-10",
                  "duration": 178
                }
                """;
        mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidFilmJson))
                .andExpect(status().isBadRequest());

        String invalidFilmJson2 = """
                {
                  "name": " ",
                  "description": "The Fellowship of the Ring",
                  "releaseDate": "2001-12-10",
                  "duration": 178
                }
                """;
        mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidFilmJson2))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldNotCreateFilmWithLongDescription() throws Exception {
        String invalidFilmJson = """
                {
                  "name": "Lord of the Ring",
                  "description": "Братство распалось, но Кольцо Всевластья должно быть уничтожено.
                   Фродо и Сэм вынуждены довериться Голлуму, который взялся провести их к вратам Мордора.
                   Громадная армия Сарумана приближается: члены братства и их союзники готовы принять бой.
                   Битва за Средиземье продолжается.",
                  "releaseDate": "2002-12-05",
                  "duration": 179
                }
                """;
        mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidFilmJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldNotCreateFilmWithIncorrectReleaseDate() throws Exception {
        String invalidFilmJson = """
                {
                  "name": "Lord of the Ring",
                  "description": "The Fellowship of the Ring",
                  "releaseDate": "1801-12-10",
                  "duration": 178
                }
                """;
        mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidFilmJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldNotCreateFilmWithIncorrectDuration() throws Exception {
        String invalidFilmJson = """
                {
                  "name": "Lord of the Ring",
                  "description": "The Fellowship of the Ring",
                  "releaseDate": "1801-12-10",
                  "duration": -178
                }
                """;
        mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidFilmJson))
                .andExpect(status().isBadRequest());
    }
}

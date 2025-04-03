package ru.yandex.practicum.filmorate.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.filmorate.dto.film.FilmDto;
import ru.yandex.practicum.filmorate.dto.film.NewFilmRequest;
import ru.yandex.practicum.filmorate.dto.film.UpdateFilmRequest;
import ru.yandex.practicum.filmorate.model.Film;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class FilmMapper {

    // Маппинг из Film в FilmDto
    public static FilmDto mapToFilmDto(Film film) {
        FilmDto dto = new FilmDto();
        dto.setId(film.getId());
        dto.setName(film.getName());
        dto.setDescription(film.getDescription());
        dto.setReleaseDate(film.getReleaseDate());
        dto.setDuration(film.getDuration());

        // Если жанры есть, добавляем их
        if (film.getGenres() != null) {
            dto.setGenres(film.getGenres());  // Добавляем список жанров
        }

        // Рейтинг
        if (film.getMpa() != null) {
            dto.setMpa(film.getMpa());
        }

        return dto;
    }

    // Маппинг из NewFilmRequest в Film
    public static Film mapToFilm(NewFilmRequest request) {
        Film film = new Film();
        film.setName(request.getName());
        film.setDescription(request.getDescription());
        film.setReleaseDate(request.getReleaseDate());
        film.setDuration(request.getDuration());

        // Если есть жанры, устанавливаем их
        if (request.getGenres() != null) {
            film.setGenres(request.getGenres());  // Список жанров
        }

        film.setMpa(request.getMpa());  // Один рейтинг для фильма
        return film;
    }

    // Обновление полей фильма в зависимости от запроса
    public static Film updateFilmFields(Film film, UpdateFilmRequest request) {
        if (request.hasName()) {
            film.setName(request.getName());
        }
        if (request.hasDescription()) {
            film.setDescription(request.getDescription());
        }
        if (request.hasReleaseDate()) {
            film.setReleaseDate(request.getReleaseDate());
        }
        if (request.hasDuration()) {
            film.setDuration(request.getDuration());
        }
        if (request.hasGenres()) {
            film.setGenres(request.getGenres());  // Обновляем список жанров
        }
        if (request.hasMpa()) {
            film.setMpa(request.getMpa());
        }
        return film;
    }
}

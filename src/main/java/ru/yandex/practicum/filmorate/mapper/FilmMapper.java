package ru.yandex.practicum.filmorate.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.dto.film.FilmDto;
import ru.yandex.practicum.filmorate.dto.film.NewFilmRequest;
import ru.yandex.practicum.filmorate.dto.film.UpdateFilmRequest;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.time.Month;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class FilmMapper {

    public static FilmDto mapToFilmDto(Film film) {
        log.debug("Конвертируем Film в FilmDto");
        FilmDto dto = new FilmDto();
        dto.setId(film.getId());
        dto.setName(film.getName());
        dto.setDescription(film.getDescription());
        dto.setReleaseDate(film.getReleaseDate());
        dto.setDuration(film.getDuration());
        if (film.getGenres() != null) {
            dto.setGenres(film.getGenres());
        }
        if (film.getMpa() != null) {
            dto.setMpa(film.getMpa());
        }
        if (film.getDirector() != null) {
            dto.setDirector(film.getDirector());
        }

        return dto;
    }

    public static Film mapToFilm(NewFilmRequest request) {
        log.info("Конвертируем запрос в Film");
        Film film = new Film();
        film.setName(request.getName());
        film.setDescription(request.getDescription());
        if (request.getReleaseDate().isBefore(LocalDate.of(1895, Month.DECEMBER, 28))) {
            log.warn("Дата релиза фильма некорректна: {}", request.getReleaseDate());
            throw new ValidationException("Дата релиза не может быть раньше 28 декабря 1895 года");
        }
        film.setReleaseDate(request.getReleaseDate());
        film.setDuration(request.getDuration());
        film.setMpa(request.getMpa());
        film.setDirector(request.getDirector());
        return film;
    }

    public static Film updateFilmFields(Film film, UpdateFilmRequest request) {
        log.debug("Обновление параметров фильма (Id: {})", film.getId());
        if (request.hasName()) {
            film.setName(request.getName());
        }
        if (request.hasDescription()) {
            film.setDescription(request.getDescription());
        }
        if (request.hasReleaseDate()) {
            if (request.getReleaseDate().isBefore(LocalDate.of(1895, Month.DECEMBER, 28))) {
                log.warn("Дата релиза фильма некорректна: {}", request.getReleaseDate());
                throw new ValidationException("Дата релиза не может быть раньше 28 декабря 1895 года");
            }
            film.setReleaseDate(request.getReleaseDate());
        }
        if (request.hasDuration()) {
            film.setDuration(request.getDuration());
        }
        if (request.hasGenres()) {
            film.setGenres(request.getGenres());
        }
        if (request.hasMpa()) {
            film.setMpa(request.getMpa());
        }
        if (request.hasDirector()){
            film.setDirector(request.getDirector());
        }
        return film;
    }
}

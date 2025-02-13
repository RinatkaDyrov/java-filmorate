package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.time.Month;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/films")
public class FilmController {

    private final Map<Long, Film> films = new HashMap<>();
    private static final LocalDate EARLIEST_RELEASE_DATE = LocalDate.of(1895, Month.DECEMBER, 28);
    private static final Logger logger = LoggerFactory.getLogger(FilmController.class);

    @GetMapping
    public Collection<Film> findAll() {
        logger.info("Выполняется запрос списка всех фильмов");
        return List.copyOf(films.values());
    }

    @PostMapping
    public Film create(@Valid @RequestBody Film film) {
        logger.info("Добавление фильма: {}", film);
        if (film.getReleaseDate().isBefore(EARLIEST_RELEASE_DATE)) {
            logger.warn("Дата релиза фильма некорректна: {}", film.getReleaseDate());
            throw new ValidationException("Дата релиза не может быть раньше 28 декабря 1895 года");
        }
        film.setId(getNextId());
        films.put(film.getId(), film);
        logger.info("Фильм добавлен: id={}, name={}", film.getId(), film.getName());
        return film;
    }


    @PutMapping
    public Film update(@Valid @RequestBody Film newFilm) {
        if (newFilm.getId() == null || !films.containsKey(newFilm.getId())) {
            logger.warn("Идентификационный номер фильма неверен или не указан ({})",
                    newFilm.getId() == null ? "null" : "blank");
            throw new NotFoundException("Неверный идентификационный номер");
        }
        if (newFilm.getReleaseDate().isBefore(EARLIEST_RELEASE_DATE)) {
            logger.warn("Новая дата релиза фильма некорректна: {}", newFilm.getReleaseDate());
            throw new ValidationException("Дата релиза не может быть раньше 28 декабря 1895 года");
        }
        Film oldFilm = films.get(newFilm.getId());
        if (newFilm.equals(oldFilm)) {
            logger.info("Поля фильма не содержат новых данных");
            return oldFilm;
        } else {
            films.put(newFilm.getId(), newFilm);
            logger.info("Поля фильма {} обновлены", newFilm.getName());
            return newFilm;
        }
    }

    private long getNextId() {
        long currentMaxId = films.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}
package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.Instant;
import java.time.LocalDate;
import java.time.Month;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/films")
public class FilmController {

    private final Map<Long, Film> films = new HashMap<>();
    private static final LocalDate EARLIEST_RELEASE_DATE = LocalDate.of(1895, Month.DECEMBER, 28);
    private static final Logger logger = LoggerFactory.getLogger(FilmController.class);

    @GetMapping
    public Collection<Film> findAll(){
        logger.info("Выполняется запрос списка всех фильмов");
        return films.values();
    }

    @PostMapping
    public Film create(@Valid @RequestBody Film film){
        validateFilm(film);
        film.setId(getNextId());
        films.put(film.getId(), film);
        return film;
    }

    @PutMapping
    public Film update(@Valid @RequestBody Film newFilm){
        if (newFilm.getId() == null || !films.containsKey(newFilm.getId())){
            throw new ValidationException("Неверный идентификационный номер");
        }
        validateFilm(newFilm);
        Film oldFilm = films.get(newFilm.getId());
        if (newFilm.equals(oldFilm)){
            return oldFilm;
        }else {
         films.put(newFilm.getId(), newFilm);
         return newFilm;
        }
    }

    private void validateFilm(Film film){
        if (film.getName() == null || film.getName().isBlank()){
            throw new ConditionsNotMetException("Название не может быть пустым");
        }
        if (film.getReleaseDate().isBefore(EARLIEST_RELEASE_DATE)){
            throw new ConditionsNotMetException("Дата релиза не может быть раньше 28 декабря 1895 года");
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
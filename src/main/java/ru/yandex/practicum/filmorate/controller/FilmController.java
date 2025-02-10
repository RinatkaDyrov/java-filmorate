package ru.yandex.practicum.filmorate.controller;

import lombok.Getter;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
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
    private static final int MAX_DESC_LENGTH = 200;
    private static final LocalDate EARLIEST_RELEASE_DATE = LocalDate.of(1895, Month.DECEMBER, 28);

    @GetMapping
    public Collection<Film> findAll(){
        return films.values();
    }

    @PostMapping
    public Film create(@RequestBody Film film){
        validateFilm(film);
        film.setId(getNextId());
        films.put(film.getId(), film);
        return film;
    }

    @PutMapping
    public Film update(@RequestBody Film newFilm){
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
        if (film.getDescription().length() > MAX_DESC_LENGTH){
            throw new ConditionsNotMetException("Максимальная длина описания — 200 символов");
        }
        if (film.getReleaseDate().isBefore(Instant.from(EARLIEST_RELEASE_DATE))){
            throw new ConditionsNotMetException("Дата релиза не может быть раньше 28 декабря 1895 года");
        }
        if(film.getDuration() < 0){
            throw new ConditionsNotMetException("Продолжительность фильма должна быть положительным числом");
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

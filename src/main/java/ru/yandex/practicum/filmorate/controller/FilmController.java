package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dto.film.FilmDto;
import ru.yandex.practicum.filmorate.dto.film.NewFilmRequest;
import ru.yandex.practicum.filmorate.dto.film.UpdateFilmRequest;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.Collection;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/films")
public class FilmController {

    private static final Logger log = LoggerFactory.getLogger(FilmController.class);
    private final FilmService filmService;

    @GetMapping
    public Collection<FilmDto> findAll() {
        log.debug("Запрос на поиск всех фильмов");
        return filmService.getAllFilms();
    }

    @GetMapping("/{id}")
    public FilmDto findFilmById(@PathVariable long id) {
        log.debug("Запрос на поиск фильма (Id: {})", id);
        return filmService.findFilmById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public FilmDto create(@Valid @RequestBody NewFilmRequest request) {
        log.debug("Добавление нового фильма");
        System.out.println();
        System.out.println(request);
        System.out.println();
        return filmService.createFilm(request);
    }

    @PutMapping
    @ResponseStatus(HttpStatus.OK)
    public FilmDto update(@Valid @RequestBody UpdateFilmRequest request) {
        log.debug("Обновляем данные пользователя (Id: {})", request.getId());
        System.out.println();
        System.out.println(request);
        System.out.println();
        return filmService.updateFilm(request.getId(), request);
    }

    @PutMapping("/{filmId}/like/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public void setLike(@PathVariable long filmId,
                        @PathVariable long userId) {
        log.debug("Пользователь (Id: {}) ставит лайк фильму (Id: {})", userId, filmId);
        filmService.setLike(userId, filmId);
    }

    @DeleteMapping("/{filmId}/like/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteLike(@PathVariable long filmId,
                           @PathVariable long userId) {
        log.debug("Пользователь (Id: {}) убирает лайк с фильма (Id: {})", userId, filmId);
        filmService.deleteLike(userId, filmId);
    }

    @GetMapping("/popular")
    @ResponseStatus(HttpStatus.OK)
    public Collection<Film> getPopularFilms(@RequestParam(defaultValue = "10") int count, @RequestParam(defaultValue = "-1") int genreId, @RequestParam(defaultValue = "-1") int year) {
        log.debug("Получаем список {} популярных фильмов", count);
        return filmService.getPopularFilms(count, genreId, year);
    }

    @GetMapping("/common")
    @ResponseStatus(HttpStatus.OK)
    public List<Film> getCommonFilms(@RequestParam Long userId, @RequestParam Long friendId) {
        log.debug("Пользователь - {} получает общие фильмы с Пользователем - {}", userId, friendId);
        return filmService.getCommonFilms(userId, friendId);
    }

    @GetMapping("/director/{directorId}")
    @ResponseStatus(HttpStatus.OK)
    public Collection<FilmDto> getDirectorsFilmsSortedByParams(@PathVariable long directorId,
                                                               @RequestParam(defaultValue = "") String sortBy) {
        String[] sortParams = sortBy.split(",");
        return filmService.getSortedFilmsByDirector(directorId, sortParams);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteFilm(@PathVariable Long id) {
        filmService.deleteFilm(id);
    }
}
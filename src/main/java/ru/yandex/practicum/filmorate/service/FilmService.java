package ru.yandex.practicum.filmorate.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Collection;
import java.util.Comparator;

@Service
public class FilmService {

    private static final Logger log = LoggerFactory.getLogger(FilmService.class);
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    @Autowired
    public FilmService(FilmStorage filmStorage, UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    public Collection<Film> findAllFilms() {
        return filmStorage.findAll();
    }

    public Film createFilm(Film newFilm) {
        return filmStorage.create(newFilm);
    }

    public Film updateFilm(Film updFilm) {
        return filmStorage.update(updFilm);
    }

    public void setLike(long filmId, long userId) {
        log.debug("Пользователь (userID: {}) ставит лайк фильму (filmID: {})", userId, filmId);
        User user = userStorage.findUserById(userId);
        Film film = filmStorage.findFilmById(filmId);

        film.getLikes().add(userId);

        filmStorage.update(film);
        log.info("Пользователь (userID: {}) поставил лайк фильму (filmID: {})", userId, filmId);
    }

    public void deleteLike(long filmId, long userId) {
        User user = userStorage.findUserById(userId);
        Film film = filmStorage.findFilmById(filmId);
        if (!film.getLikes().contains(userId)) {
            log.warn("Попытка удаления несуществующего лайка");
            throw new NotFoundException("На данном фильме не стоит ваш лайк");
        }
        film.getLikes().remove(userId);

        filmStorage.update(film);
        log.info("Пользователь (userID: {}) удалил лайк у фильма (filmID: {})", userId, filmId);
    }

    public Collection<Film> getPopularFilms(int count) {
        log.debug("Получение списка популярных фильмов");
        Collection<Film> allFilms = filmStorage.findAll();

        return allFilms.stream()
                .sorted(Comparator.comparing((Film film) -> film.getLikes().size()).reversed())
                .limit(Math.min(count, allFilms.size()))
                .toList();
    }
}
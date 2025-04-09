package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dal.like.LikeRepository;
import ru.yandex.practicum.filmorate.dto.film.FilmDto;
import ru.yandex.practicum.filmorate.dto.film.NewFilmRequest;
import ru.yandex.practicum.filmorate.dto.film.UpdateFilmRequest;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.mapper.FilmMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.interfaces.FilmStorage;
import ru.yandex.practicum.filmorate.storage.interfaces.UserStorage;

import java.util.Collection;
import java.util.stream.Collectors;

@Slf4j
@Service
public class FilmService {

    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    @Autowired
    public FilmService(@Qualifier("filmDbStorage") FilmStorage filmStorage,
                       @Qualifier("userDbStorage") UserStorage userStorage, LikeRepository likeRepository) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    public Collection<FilmDto> getAllFilms() {
        return filmStorage.findAll()
                .stream()
                .map(FilmMapper::mapToFilmDto)
                .collect(Collectors.toList());
    }

    public FilmDto createFilm(NewFilmRequest request) {
        if (request.getName().isEmpty()) {
            throw new ConditionsNotMetException("Название фильма должно быть указано");
        }
        if (request.getDescription().isEmpty()) {
            throw new ConditionsNotMetException("Описание фильма должно быть указано");
        }
        Film film = FilmMapper.mapToFilm(request);
        if (request.getGenres() != null && !request.getGenres().isEmpty()) {
            film.setGenres(request.getGenres());
        }
        film = filmStorage.create(film);

        return FilmMapper.mapToFilmDto(film);
    }


    public FilmDto updateFilm(long id, UpdateFilmRequest request) {
        log.info("Обновление фильма в сервисе");
        Film updFilm = filmStorage.findFilmById(id);
        if (updFilm == null) {
            log.warn("Фильм id {} не найден", id);
            throw new NotFoundException("Фильм с таким id не найден");
        }

        updFilm = FilmMapper.updateFilmFields(updFilm, request);

        if (request.hasGenres()) {
            updFilm.setGenres(request.getGenres());
        }

        updFilm = filmStorage.update(updFilm);
        return FilmMapper.mapToFilmDto(updFilm);
    }


    public void setLike(long userId, long filmId) {
        log.debug("Пользователь (userID: {}) ставит лайк фильму (filmID: {})", userId, filmId);

        boolean success = filmStorage.addLike(userId, filmId);
        if (success) {
            log.debug("Пользователь (userID: {}) поставил лайк фильму (filmID: {})", userId, filmId);
        } else {
            log.warn("Ошибка при попытке поставить лайк. userId: {}, filmId: {}", userId, filmId);
            throw new RuntimeException("Не удалось поставить лайк.");
        }
    }

    public void deleteLike(long userId, long filmId) {
        log.debug("Удаление");
        User user = userStorage.findUserById(userId);
        Film film = filmStorage.findFilmById(filmId);
        boolean success = filmStorage.removeLike(userId, filmId);
        if (success) {
            log.debug("Пользователь (userID: {}) удалил лайк у фильма (filmID: {})", userId, filmId);
        } else {
            log.warn("Ошибка при удалении лайка. userId: {}, filmId: {}", userId, filmId);
            throw new RuntimeException("Не удалось удалить лайк.");
        }
    }

    public Collection<Film> getPopularFilms(int count) {
        log.debug("Получение списка популярных фильмов");
        return filmStorage.getPopularFilms(count);
    }

    public Collection<Film> getPopularFilms(int count, int genreId, int year) {
        log.debug("Получение списка популярных фильмов");
        if (genreId == -1 && year == -1) {
            return filmStorage.getPopularFilms(count);
        }
        return filmStorage.getPopularFilms(count, genreId, year);
    }

    public FilmDto findFilmById(long id) {
        return FilmMapper.mapToFilmDto(filmStorage.findFilmById(id));
    }
}
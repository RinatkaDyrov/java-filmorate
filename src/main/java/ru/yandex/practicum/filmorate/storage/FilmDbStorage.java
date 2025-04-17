package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dal.event.EventRepository;
import ru.yandex.practicum.filmorate.dal.film.FilmRepository;
import ru.yandex.practicum.filmorate.dal.like.LikeRepository;
import ru.yandex.practicum.filmorate.dal.user.UserRepository;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.interfaces.FilmStorage;

import java.util.Collection;
import java.util.List;

@Slf4j
@Component("filmDbStorage")
@RequiredArgsConstructor
public class FilmDbStorage implements FilmStorage {

    private final FilmRepository filmRepository;
    private final UserRepository userRepository;
    private final LikeRepository likeRepository;
    private final EventRepository eventRepository;


    @Override
    public Collection<Film> findAll() {
        return filmRepository.findAllFilms();
    }

    @Override
    public Film findFilmById(long filmId) {
        log.debug("Поиск фильма в хранилище");
        return filmRepository.getFilmById(filmId)
                .orElseThrow(() -> new NotFoundException("Фильм с ID " + filmId + " не найден"));
    }

    @Override
    public Film create(Film newFilm) {
        log.debug("Добавление фильма в хранилище");
        return filmRepository.save(newFilm);
    }

    @Override
    public Film update(Film updFilm) {
        log.debug("Обновление фильма в хранилище");
        return filmRepository.updateFilm(updFilm);
    }

    @Override
    public Collection<Film> findFilmByGenre(String genre) {
        log.debug("Поиск фильма по жанру в хранилище");
        return filmRepository.findFilmByGenre(genre);
    }

    @Override
    public Collection<Film> findFilmByRating(String rating) {
        log.debug("Поиск фильма по рейтингу в хранилище");
        return filmRepository.findFilmByRating(rating);
    }

    @Override
    public boolean addLike(long userId, long filmId) {
        log.debug("Добавление лайка в хранилище");
        if (userRepository.findUserById(userId).isEmpty()) {
            throw new NotFoundException("Пользователь не найден");
        }
        if (filmRepository.getFilmById(filmId).isEmpty()) {
            throw new NotFoundException("Фильм не найден");
        }
        if (likeRepository.isThisPairExist(userId, filmId)) {
            throw new ConditionsNotMetException("У вас уже стоит лайк на данном фильме");
        }
        eventRepository.addLikeEvent(userId, filmId);
        return likeRepository.addLike(userId, filmId);
    }

    @Override
    public boolean removeLike(long userId, long filmId) {
        log.debug("Удаление лайка в хранилище");
        eventRepository.removeLikeEvent(userId, filmId);
        return likeRepository.deleteLike(userId, filmId);
    }

    @Override
    public int countLikes(long filmId) {
        log.debug("Запрос кол-ва лайков в хранилище");
        return likeRepository.getLikeCount(filmId);
    }

    @Override
    public Collection<Film> getPopularFilms(int count) {
        log.debug("Запрос популярных фильмов в хранилище");
        return likeRepository.findPopularFilms(count);
    }

    @Override
    public Collection<Film> getPopularFilms(int count, int genreId, int year) {
        log.debug("Запрос популярных фильмов в хранилище");
        return likeRepository.findPopularFilms(count, genreId, year);
    }

    @Override
    public List<Film> getCommonFilms(Long userId, Long friendId) {
        log.info("Запрос общих фильмов");
        return filmRepository.getCommonFilms(userId, friendId);
    }

    @Override
    public Collection<Film> getSortedFilmsByDirector(long directorId, String[] sortParams) {
        return filmRepository.getSortedFilmsByDirector(directorId, sortParams);
    }

    @Override
    public Collection<Film> searchFilmsByTitle(String query) {
        log.debug("Поиск фильмов по названию в хранилище");
        return filmRepository.searchFilmsByTitle(query);
    }

    @Override
    public Collection<Film> searchFilmsByDirector(String query) {
        log.debug("Поиск фильмов по режиссеру в хранилище");
        return filmRepository.searchFilmsByDirector(query);
    }

    @Override
    public Collection<Film> searchFilmsByTitleAndDirector(String query) {
        log.debug("Поиск фильмов по названию и по режиссеру в хранилище");
        return filmRepository.searchFilmsByTitleAndDirector(query);
    }
}
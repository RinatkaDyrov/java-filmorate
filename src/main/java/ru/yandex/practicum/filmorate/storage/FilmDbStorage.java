package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dal.film.FilmRepository;
import ru.yandex.practicum.filmorate.dal.like.LikeRepository;
import ru.yandex.practicum.filmorate.dal.user.UserRepository;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;

@Slf4j
@Component("filmDbStorage")
public class FilmDbStorage implements FilmStorage {

    private final FilmRepository filmRepository;
    private final UserRepository userRepository;
    private final LikeRepository likeRepository;

    public FilmDbStorage(FilmRepository filmRepository, UserRepository userRepository, LikeRepository likeRepository) {
        this.filmRepository = filmRepository;
        this.userRepository = userRepository;
        this.likeRepository = likeRepository;
    }

    @Override
    public Collection<Film> findAll() {
        return filmRepository.findAllFilms();
    }

    @Override
    public Film findFilmById(long filmId) {
        return filmRepository.getFilmById(filmId)
                .orElseThrow(() -> new NotFoundException("Фильм с ID " + filmId + " не найден"));
    }

    @Override
    public Film create(Film newFilm) {
        return filmRepository.save(newFilm);
    }

    @Override
    public Film update(Film updFilm) {
        log.debug("Обновление фильма в хранилище");

        return filmRepository.updateFilm(updFilm);
    }

    @Override
    public Collection<Film> findFilmByGenre(String genre) {
        return filmRepository.findFilmByGenre(genre);
    }

    @Override
    public Collection<Film> findFilmByRating(String rating) {
        return filmRepository.findFilmByRating(rating);
    }

    @Override
    public boolean addLike(long userId, long filmId) {
        if (userRepository.findUserById(userId).isEmpty()) {
            throw new NotFoundException("Пользователь не найден");
        }
        if (filmRepository.getFilmById(filmId).isEmpty()) {
            throw new NotFoundException("Фильм не найден");
        }
        if (likeRepository.isThisPairExist(userId, filmId)) {
            throw new ConditionsNotMetException("У вас уже стоит лайк на данном фильме");
        }
        return likeRepository.addLike(userId, filmId);
    }

    @Override
    public boolean removeLike(long userId, long filmId) {
        return likeRepository.deleteLike(userId, filmId);
    }

    @Override
    public int countLikes(long filmId) {
        return likeRepository.getLikeCount(filmId);
    }

    @Override
    public Collection<Film> getPopularFilms(int count) {
        return likeRepository.findPopularFilms(count);
    }
}
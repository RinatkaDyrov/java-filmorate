package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dal.film.FilmRepository;
import ru.yandex.practicum.filmorate.dal.like.LikeRepository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.List;

@Component("filmDbStorage")
public class FilmDbStorage implements FilmStorage {

    private final FilmRepository filmRepository;
    private final LikeRepository likeRepository;

    public FilmDbStorage(FilmRepository filmRepository, LikeRepository likeRepository) {
        this.filmRepository = filmRepository;
        this.likeRepository = likeRepository;
    }

    @Override
    public Collection<Film> findAll() {
        return filmRepository.findALlFilms();
    }

    @Override
    public Film findFilmById(long filmId) {
        return filmRepository.findFilmById(filmId)
                .orElseThrow(() -> new NotFoundException("Фильм с ID " + filmId + " не найден"));
    }

    @Override
    public Film create(Film newFilm) {
        return filmRepository.save(newFilm);
    }

    @Override
    public Film update(Film updFilm) {
        return filmRepository.update(updFilm);
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
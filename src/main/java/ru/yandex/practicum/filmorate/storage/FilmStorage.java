package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;

public interface FilmStorage {

    Collection<Film> findAll();

    Film findFilmById(long filmId);

    Film create(Film newFilm);

    Film update(Film updFilm);

    default Collection<Film> findFilmByGenre(String genre) {
        throw new UnsupportedOperationException("Не поддерживается в данном хранилище");
    }

    default Collection<Film> findFilmByRating(String rating) {
        throw new UnsupportedOperationException("Не поддерживается в данном хранилище");
    }

    default boolean addLike(long userId, long filmId) {
        throw new UnsupportedOperationException("Не поддерживается в данном хранилище");
    }

    default boolean removeLike(long userId, long filmId) {
        throw new UnsupportedOperationException("Не поддерживается в данном хранилище");
    }

    default int countLikes(long filmId) {
        throw new UnsupportedOperationException("Не поддерживается в данном хранилище");
    }

    default Collection<Film> getPopularFilms(int count) {
        throw new UnsupportedOperationException("Не поддерживается в данном хранилище");
    }
}

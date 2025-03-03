package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;

public interface FilmStorage {

    Collection<Film> findAll();

    Film findFilmById(long filmId);

    Film create(Film newFilm);

    Film update(Film updFilm);
}

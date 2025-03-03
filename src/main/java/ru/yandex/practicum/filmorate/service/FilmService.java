package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Collection;
import java.util.Comparator;
import java.util.stream.Collectors;

@Service
public class FilmService {

    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    @Autowired
    public FilmService(FilmStorage filmStorage, UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    public Collection<Film> findAllFilms(){
        return filmStorage.findAll();
    }

    public Film createFilm(Film newFilm){
        return filmStorage.create(newFilm);
    }

    public Film updateFilm(Film updFilm){
        return filmStorage.update(updFilm);
    }

    public void setLike(long filmId, long userId){
        User user = userStorage.findUserById(userId);
        Film film = filmStorage.findFilmById(filmId);

        film.getLikes().add(userId);

        filmStorage.update(film);
    }

    public void deleteLike(long filmId, long userId){
        User user = userStorage.findUserById(userId);
        Film film = filmStorage.findFilmById(filmId);
        if (!film.getLikes().contains(userId)){
            throw new NotFoundException("На данном фильме не стоит ваш лайк");
        }
        film.getLikes().remove(userId);

        filmStorage.update(film);
    }

    public Collection<Film> getPopularFilms(String count){
        int countInt;

        try {
            countInt = Integer.parseInt(count);
        }catch (NumberFormatException e){
            throw new ValidationException("Неверный формат количества запрашиваемых фильмов");
        }

        Collection<Film> allFilms = filmStorage.findAll();

        return allFilms.stream()
                .sorted(Comparator.comparing((Film film) -> film.getLikes().size()).reversed())
                .limit(Math.min(countInt, allFilms.size()))
                .toList();
    }
}
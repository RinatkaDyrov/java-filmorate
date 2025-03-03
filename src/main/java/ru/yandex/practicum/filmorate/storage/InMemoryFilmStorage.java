package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.time.Month;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class InMemoryFilmStorage implements FilmStorage{

    private final Map<Long, Film> films = new HashMap<>();
    private static final LocalDate EARLIEST_RELEASE_DATE = LocalDate.of(1895, Month.DECEMBER, 28);

    @Override
    public Collection<Film> findAll() {
        return List.copyOf(films.values());
    }

    @Override
    public Film findFilmById(long filmId) {
        Film film = films.get(filmId);
        if (film == null){
            throw new NotFoundException("Фильм  с ID " + filmId + " не найден");
        }
        return film;
    }

    @Override
    public Film create(Film film) {
        validateReleaseDate(film.getReleaseDate());
        film.setId(getNextId());
        films.put(film.getId(), film);
        log.info("Фильм добавлен: id={}, name={}", film.getId(), film.getName());
        return film;
    }

    @Override
    public Film update(Film newFilm) {
        if (newFilm.getId() == null || !films.containsKey(newFilm.getId())) {
            log.warn("Идентификационный номер фильма неверен или не указан ({})",
                    newFilm.getId() == null ? "null" : "blank");
            throw new NotFoundException("Неверный идентификационный номер");
        }
        validateReleaseDate(newFilm.getReleaseDate());
        Film oldFilm = films.get(newFilm.getId());
        if (newFilm.equals(oldFilm)) {
            log.info("Поля фильма не содержат новых данных");
            return oldFilm;
        } else {
            films.put(newFilm.getId(), newFilm);
            log.info("Поля фильма {} обновлены", newFilm.getName());
            return newFilm;
        }
    }

    private void validateReleaseDate(LocalDate releaseDate) {
        if (releaseDate.isBefore(EARLIEST_RELEASE_DATE)) {
            log.warn("Дата релиза фильма некорректна: {}", releaseDate);
            throw new ValidationException("Дата релиза не может быть раньше 28 декабря 1895 года");
        }
    }

    private long getNextId() {
        log.debug("Генерация идентификационного номера фильма");
        long currentMaxId = films.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}
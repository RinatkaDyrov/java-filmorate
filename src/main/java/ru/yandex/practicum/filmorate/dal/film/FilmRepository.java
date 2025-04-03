package ru.yandex.practicum.filmorate.dal.film;

import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dal.BaseRepository;
import ru.yandex.practicum.filmorate.dal.genre.GenreRowMapper;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.*;

@Slf4j
@Repository
public class FilmRepository extends BaseRepository<Film> {

    private static final String FIND_ALL_QUERY = "SELECT film_id, name, description, release_date, duration, mpa_id FROM films";
    private static final String FIND_BY_ID_QUERY = "SELECT film_id, name, description, release_date, duration, mpa_id FROM films WHERE film_id=?";
    private static final String INSERT_QUERY = "INSERT INTO films (name, description, release_date, duration, rating_id) VALUES (?, ?, ?, ?, ?)";
    private static final String UPDATE_QUERY = "UPDATE films " +
            "SET name = ?, description = ?, release_date = ?, duration = ?, rating_id = ? " +
            "WHERE id = ?";
    private static final String FIND_BY_GENRE_QUERY = "SELECT f.* FROM films f " +
            "JOIN genre g ON f.genre_id = g.id WHERE g.name = ?";
    private static final String FIND_BY_RATING_QUERY = "SELECT f.* FROM films f " +
            "JOIN rating_mpa r ON f.rating_id = r.id WHERE r.name = ?";
    private static final String FIND_GENRES_BY_FILM_ID_QUERY =
            "SELECT g.id AS genre_id, g.name AS genre_name " +
                    "FROM genre g JOIN film_genre fg ON g.id = fg.genre_id WHERE fg.film_id = ?";
    private static final String FIND_FILMS_BY_RATING_QUERY = "SELECT f.* FROM films f JOIN rating_mpa r ON f.rating_id = r.id WHERE r.id = ?";

    public FilmRepository(JdbcTemplate jdbc, RowMapper<Film> mapper) {
        super(jdbc, mapper);
    }

    public List<Film> findAllFilms() {
        return jdbc.query(FIND_ALL_QUERY, new FilmRowMapper());
    }

    public Optional<Film> getFilmById(long id) {
        log.debug("getFilmById({})", id);
        try {
            Optional<Film> thisFilm = findOne(FIND_BY_ID_QUERY, id);
            log.trace("The movie {} was returned", thisFilm);
            return thisFilm;
        } catch (EmptyResultDataAccessException e) {
            log.warn("No film found with id {}", id);
            throw new NotFoundException("Film with id " + id + " not found");
        }
    }

    public Collection<Film> findFilmByGenre(String genre) {
        return findMany(FIND_BY_GENRE_QUERY, genre);
    }

    public Collection<Film> findFilmByRating(String rating) {
        return findMany(FIND_BY_RATING_QUERY, rating);
    }

    public Film save(Film film) {
        // Проверяем, существует ли такой MPA
        long mpaId = film.getMpa().getId();
        Integer count = jdbc.queryForObject("SELECT COUNT(*) FROM rating_mpa WHERE id = ?", Integer.class, mpaId);
        if (count == null || count == 0) {
            throw new NotFoundException("MPA with ID " + mpaId + " wasn't found");
        }

        // Вставляем фильм и получаем его ID
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbc.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(
                    INSERT_QUERY,
                    Statement.RETURN_GENERATED_KEYS
            );
            ps.setString(1, film.getName());
            ps.setString(2, film.getDescription());
            ps.setDate(3, Date.valueOf(film.getReleaseDate()));
            ps.setInt(4, film.getDuration());
            ps.setLong(5, mpaId);
            return ps;
        }, keyHolder);

        // Устанавливаем ID фильма
        film.setId(keyHolder.getKey().longValue());

        // Добавляем жанры
        log.info("Genres to save: {}", film.getGenres());
        if (film.getGenres() != null && !film.getGenres().isEmpty()) {
            for (Genre genre : film.getGenres()) {
                Integer genreCount = jdbc.queryForObject(
                        "SELECT COUNT(*) FROM genre WHERE id = ?", Integer.class, genre.getId()
                );
                if (genreCount == null || genreCount == 0) {
                    throw new NotFoundException("Genre with ID " + genre.getId() + " wasn't found");
                }
                jdbc.update("INSERT INTO film_genre (film_id, genre_id) VALUES (?, ?)", film.getId(), genre.getId());
            }
        }

        return film;
    }

    public Film updateFilm(Film film) {
        log.debug("updateFilm({})", film);

        // Проверяем, существует ли фильм
        Integer count = jdbc.queryForObject("SELECT COUNT(*) FROM films WHERE id = ?", Integer.class, film.getId());
        if (count == null || count == 0) {
            throw new NotFoundException("Attempt to update non-existing movie with id " + film.getId());
        }

        // Обновляем основные данные фильма
        jdbc.update(
                "UPDATE films SET name=?, description=?, release_date=?, duration=?, rating_id=? WHERE id=?",
                film.getName(),
                film.getDescription(),
                Date.valueOf(film.getReleaseDate()),
                film.getDuration(),
                film.getMpa().getId(),
                film.getId()
        );

        // Обновляем жанры (удаляем старые, вставляем новые)
        jdbc.update("DELETE FROM film_genre WHERE film_id = ?", film.getId());
        if (film.getGenres() != null && !film.getGenres().isEmpty()) {
            for (Genre genre : new HashSet<>(film.getGenres())) {
                jdbc.update("INSERT INTO film_genre (film_id, genre_id) VALUES (?, ?)", film.getId(), genre.getId());
            }
        }

        log.trace("The movie {} was updated in the database", film);
        return film;
    }

    private void addGenres(int filmId, Set<Genre> genres) {
        log.debug("addGenres({}, {})", filmId, genres);
        Set<Genre> uniqueGenres = new HashSet<>();

        for (Genre genre : genres) {
            if (uniqueGenres.add(genre)) {
                jdbc.update("INSERT INTO film_genre (film_id, genre_id) VALUES (?, ?)", filmId, genre.getId());
                log.trace("Genre {} was added to movie {}", genre.getName(), filmId);
            } else {
                log.trace("Duplicate genre {} found in input and will not be added", genre.getName());
            }
        }
    }
    private void updateGenres(int filmId, Set<Genre> genres) {
        log.debug("updateGenres({}, {})", filmId, genres);
        deleteGenres(filmId);
        addGenres(filmId, genres);
    }

    public Set<Genre> getGenres(int filmId) {
        log.debug("getGenres({})", filmId);
        Set<Genre> genres = new HashSet<>(jdbc.query(
                "SELECT f.genre_id, g.genre_type FROM film_genre AS f " +
                        "LEFT OUTER JOIN genre AS g ON f.genre_id = g.genre_id WHERE f.film_id=? ORDER BY g.genre_id",
                new GenreRowMapper(), filmId));
        log.trace("Genres for the movie with id {} were returned", filmId);
        return genres;
    }

    public List<Film> getAllFilmsWithGenres() {
        log.debug("getAllFilmsWithGenres()");
        String sql = "SELECT f.film_id, f.name, f.description, f.release_date, f.duration, f.mpa_id, " +
                "g.genre_id, g.genre_type FROM films AS f " +
                "LEFT JOIN film_genre AS fg ON f.film_id = fg.film_id " +
                "LEFT JOIN genre AS g ON fg.genre_id = g.genre_id " +
                "ORDER BY f.film_id";

        List<Film> films = jdbc.query(sql, new FilmRowMapper());
        log.trace("Returned {} films with genres from the database", films.size());
        return films;
    }

    private void deleteGenres(int filmId) {
        log.debug("deleteGenres({})", filmId);
        jdbc.update("DELETE FROM film_genre WHERE film_id=?", filmId);
        log.trace("All genres were removed for a movie with id {}", filmId);
    }

    public boolean isContains(int id) {
        log.debug("isContains({})", id);
        try {
            getFilmById(id);
            log.trace("The movie with id {} was found", id);
            return true;
        } catch (EmptyResultDataAccessException exception) {
            log.trace("No information has been found for id {}", id);
            return false;
        }
    }

    public void deleteFilm(int id) {
        log.debug("deleteFilm({})", id);
        int rowsAffected = jdbc.update("DELETE FROM films WHERE film_id = ?", id);

        if (rowsAffected > 0) {
            log.trace("The movie with id {} was deleted from the database", id);
        } else {
            log.warn("Attempted to delete a movie with id {} that does not exist", id);
            throw new EntityNotFoundException("Film with id " + id + " not found");
        }
    }
}

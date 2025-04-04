package ru.yandex.practicum.filmorate.dal.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dal.BaseRepository;
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

    private static final String FIND_ALL_QUERY = "SELECT * FROM films";
    private static final String FIND_BY_ID_QUERY = "SELECT id, name, description, release_date, duration, rating_id FROM films WHERE id=?";
    private static final String INSERT_QUERY = "INSERT INTO films (name, description, release_date, duration, rating_id) VALUES (?, ?, ?, ?, ?)";
    private static final String FIND_BY_GENRE_QUERY = "SELECT f.* FROM films f " +
            "JOIN genre g ON f.genre_id = g.id WHERE g.name = ?";
    private static final String FIND_BY_RATING_QUERY = "SELECT f.* FROM films f " +
            "JOIN rating_mpa r ON f.rating_id = r.id WHERE r.name = ?";

    public FilmRepository(JdbcTemplate jdbc, RowMapper<Film> mapper) {
        super(jdbc, mapper);
    }

    public List<Film> findAllFilms() {
        return findMany(FIND_ALL_QUERY);
    }

    public Optional<Film> getFilmById(long id) {
        try {
            Optional<Film> thisFilm = findOne(FIND_BY_ID_QUERY, id);
            thisFilm.ifPresent(this::setGenreAndRatingToFilm);
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
        long mpaId = film.getMpa().getId();
        Integer count = jdbc.queryForObject("SELECT COUNT(*) FROM rating_mpa WHERE id = ?", Integer.class, mpaId);
        if (count == null || count == 0) {
            throw new NotFoundException("MPA with ID " + mpaId + " wasn't found");
        }

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

        film.setId(keyHolder.getKey().longValue());

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
        setGenreAndRatingToFilm(film);

        return film;
    }

    public Film updateFilm(Film film) {
        log.debug("Обновление фильма {} в репозитории", film);

        Integer count = jdbc.queryForObject("SELECT COUNT(*) FROM films WHERE id = ?", Integer.class, film.getId());
        if (count == null || count == 0) {
            throw new NotFoundException("Attempt to update non-existing movie with id " + film.getId());
        }

        jdbc.update(
                "UPDATE films SET name=?, description=?, release_date=?, duration=?, rating_id=? WHERE id=?",
                film.getName(),
                film.getDescription(),
                Date.valueOf(film.getReleaseDate()),
                film.getDuration(),
                film.getMpa().getId(),
                film.getId()
        );

        jdbc.update("DELETE FROM film_genre WHERE film_id = ?", film.getId());
        if (film.getGenres() != null && !film.getGenres().isEmpty()) {
            for (Genre genre : film.getGenres()) {
                jdbc.update("INSERT INTO film_genre (film_id, genre_id) VALUES (?, ?)", film.getId(), genre.getId());
            }
        }

        setGenreAndRatingToFilm(film);

        log.debug("Фильм {} был обновлен в базе данных", film);
        return film;
    }

    private void setGenreAndRatingToFilm(Film film) {
        // Подтягиваем MPA
        String mpaQuery = "SELECT id, name FROM rating_mpa WHERE id = ?";
        Mpa mpa = jdbc.queryForObject(mpaQuery, (rs, rowNum) -> {
            Mpa mpaObj = new Mpa();
            mpaObj.setId(rs.getLong("id"));
            mpaObj.setName(rs.getString("name"));
            return mpaObj;
        }, film.getMpa().getId());
        film.setMpa(mpa);

        // Подтягиваем жанры
        String genreQuery = "SELECT g.id, g.name FROM genre g " +
                "JOIN film_genre fg ON g.id = fg.genre_id WHERE fg.film_id = ? ORDER BY g.id ASC";
        List<Genre> genres = jdbc.query(genreQuery, (rs, rowNum) -> {
            Genre genre = new Genre();
            genre.setId(rs.getInt("id"));
            genre.setName(rs.getString("name"));
            return genre;
        }, film.getId());
        film.setGenres(new LinkedHashSet<>(genres));
    }

}

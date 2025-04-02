package ru.yandex.practicum.filmorate.dal.film;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dal.BaseRepository;
import ru.yandex.practicum.filmorate.model.Film;

import java.sql.Date;
import java.util.List;
import java.util.Optional;

@Repository
public class FilmRepository extends BaseRepository<Film> {

    private static final String FIND_ALL_QUERY = "SELECT * FROM films";
    private static final String FIND_BY_ID_QUERY = "SELECT * FROM films WHERE id = ?";
    private static final String INSERT_QUERY = "INSERT INTO films(name, description, releaseDate, duration)" +
            "VALUES (?, ?, ?, ?) returning id";
    private static final String UPDATE_QUERY = "UPDATE films SET name = ?, description = ?, releaseDate = ?," +
            " duration =? WHERE id = ?";
    private static final String FIND_BY_GENRE_QUERY = "SELECT f.* FROM films f " +
            "JOIN genre g ON f.genre_id = g.id WHERE g.name = ?";

    private static final String FIND_BY_RATING_QUERY = "SELECT f.* FROM films f " +
            "JOIN rating_mba r ON f rating_id = r.id WHERE r.name = ?";

    public FilmRepository(JdbcTemplate jdbc, RowMapper<Film> mapper) {
        super(jdbc, mapper);
    }

    public List<Film> findALlFilms() {
        return findMany(FIND_ALL_QUERY);
    }

    public Optional<Film> findFilmById(long id) {
        return findOne(FIND_BY_ID_QUERY, id);
    }

    public Optional<Film> findFilmByGenre(String genre) {
        return findOne(FIND_BY_GENRE_QUERY, genre);
    }

    public Optional<Film> findFilmByRating(String rating) {
        return findOne(FIND_BY_RATING_QUERY, rating);
    }

    public Film save(Film film) {
        long id = insert(INSERT_QUERY,
                film.getName(),
                film.getDescription(),
                Date.valueOf(film.getReleaseDate()),
                film.getDuration());
        film.setId(id);
        return film;
    }

    public Film update(Film film) {
        update(UPDATE_QUERY,
                film.getName(),
                film.getDescription(),
                Date.valueOf(film.getReleaseDate()),
                film.getDuration(),
                film.getId());
        return film;
    }
}

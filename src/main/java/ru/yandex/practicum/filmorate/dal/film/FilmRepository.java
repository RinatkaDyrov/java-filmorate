package ru.yandex.practicum.filmorate.dal.film;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dal.BaseRepository;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.Date;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public class FilmRepository extends BaseRepository<Film> {

    private static final String FIND_ALL_QUERY =
            "SELECT f.id AS film_id, f.name AS film_name, f.description AS film_description, " +
                    "f.release_date AS film_release_date, f.duration AS film_duration, r.id AS rating_id, r.name AS rating_name " +
                    "FROM films f LEFT JOIN rating_mpa r ON f.rating_id = r.id";
    private static final String FIND_BY_ID_QUERY = "SELECT * FROM films WHERE id = ?";
    private static final String INSERT_QUERY = "INSERT INTO films(name, description, release_date, duration, genre_id, rating_id) " +
            "VALUES (?, ?, ?, ?, ?, ?)";
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

    public FilmRepository(JdbcTemplate jdbc, RowMapper<Film> mapper) {
        super(jdbc, mapper);
    }

    public List<Film> findAllFilms() {
        List<Film> films = jdbc.query(FIND_ALL_QUERY, (rs, rowNum) -> {
            Film film = new Film();
            film.setId(rs.getLong("film_id"));
            film.setName(rs.getString("film_name"));
            film.setDescription(rs.getString("film_description"));
            Date releaseDate = rs.getDate("film_release_date");
            film.setReleaseDate(releaseDate.toLocalDate());
            film.setDuration(rs.getInt("film_duration"));

            // Получаем рейтинг
            Mpa rating = new Mpa();
            rating.setId(rs.getInt("rating_id"));
            rating.setName(rs.getString("rating_name"));
            film.setMpa(rating);

            // Получаем жанры
            List<Genre> genres = getGenresForFilm(film.getId());
            film.setGenres(genres);

            return film;
        });

        return films;
    }

    private List<Genre> getGenresForFilm(long filmId) {
        return jdbc.query(FIND_GENRES_BY_FILM_ID_QUERY, (rs, rowNum) -> {
            Genre genre = new Genre();
            genre.setId(rs.getInt("genre_id"));
            genre.setName(rs.getString("genre_name"));
            return genre;
        }, filmId);
    }

    public Optional<Film> findFilmById(long id) {
        return findOne(FIND_BY_ID_QUERY, id);
    }

    public Collection<Film> findFilmByGenre(String genre) {
        return findMany(FIND_BY_GENRE_QUERY, genre);
    }

    public Collection<Film> findFilmByRating(String rating) {
        return findMany(FIND_BY_RATING_QUERY, rating);
    }

    public Film save(Film film) {
        Long ratingId = (film.getMpa() != null) ? film.getMpa().getId() : null;

        long id = insert(INSERT_QUERY,
                film.getName(),
                film.getDescription(),
                Date.valueOf(film.getReleaseDate()),
                film.getDuration(),
                ratingId);  // сохраняем только один рейтинг (MPA)

        film.setId(id);

        if (film.getGenres() != null && !film.getGenres().isEmpty()) {
            for (Genre genre : film.getGenres()) {
                // Пример: сохраняем связь между фильмом и жанром в таблице "film_genre"
                String insertGenreQuery = "INSERT INTO film_genre (film_id, genre_id) VALUES (?, ?)";
                jdbc.update(insertGenreQuery, id, genre.getId());  // Записываем связь между фильмом и жанром
            }
        }
        return film;
//        KeyHolder keyHolder = new GeneratedKeyHolder();
//        jdbc.update(connection -> {
//            PreparedStatement ps = connection.prepareStatement(INSERT_QUERY, Statement.RETURN_GENERATED_KEYS);
//            ps.setString(1, film.getName());
//            ps.setString(2,  film.getDescription());
//            ps.setDate(3, Date.valueOf(film.getReleaseDate()));
//            ps.setInt(4, film.getDuration());
//            return ps;
//        }, keyHolder);
//        film.setId(keyHolder.getKey().longValue());
//        return film;
    }

    public Film update(Film film) {
        // 1. Обновляем основные данные фильма
        Long ratingId = (film.getMpa() != null) ? film.getMpa().getId() : null;

        update(UPDATE_QUERY,
                film.getName(),
                film.getDescription(),
                Date.valueOf(film.getReleaseDate()),
                film.getDuration(),
                ratingId,  // Обновляем рейтинг
                film.getId());  // Используем ID фильма для обновления

        // 2. Удаляем старые жанры, если они есть
        String deleteGenresQuery = "DELETE FROM film_genre WHERE film_id = ?";
        jdbc.update(deleteGenresQuery, film.getId());

        // 3. Добавляем новые жанры
        if (film.getGenres() != null && !film.getGenres().isEmpty()) {
            for (Genre genre : film.getGenres()) {
                String insertGenreQuery = "INSERT INTO film_genre (film_id, genre_id) VALUES (?, ?)";
                jdbc.update(insertGenreQuery, film.getId(), genre.getId());
            }
        }

        return film;
    }

}

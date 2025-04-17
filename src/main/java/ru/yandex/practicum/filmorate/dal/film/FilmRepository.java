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
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Repository
public class FilmRepository extends BaseRepository<Film> {

    private static final String FIND_ALL_QUERY = "SELECT * FROM films";
    private static final String FIND_BY_ID_QUERY = "SELECT id, name, description, release_date, duration, rating_id FROM films WHERE id=?";
    private static final String INSERT_QUERY = "INSERT INTO films (name, description, release_date, duration, rating_id) VALUES (?, ?, ?, ?, ?)";
    private static final String FIND_BY_GENRE_QUERY = "SELECT f.* FROM films f " + "JOIN genre g ON f.genre_id = g.id WHERE g.name = ?";
    private static final String FIND_BY_RATING_QUERY = "SELECT f.* FROM films f " + "JOIN rating_mpa r ON f.rating_id = r.id WHERE r.name = ?";
    private static final String COUNT_OF_RATINGS_QUERY = "SELECT COUNT(*) FROM rating_mpa WHERE id = ?";
    private static final String FIND_GENRES_BY_FILM_QUERY = """
            SELECT g.id, g.name FROM genre g JOIN film_genre fg
            ON g.id = fg.genre_id WHERE fg.film_id = ? ORDER BY g.id ASC
            """;
    private static final String FIND_MPA_RATINGS_QUERY = "SELECT id, name FROM rating_mpa WHERE id = ?";
    private static final String FIND_DIRECTORS_QUERY = """
            SELECT d.* FROM directors d
            JOIN film_directors fd ON fd.director_id = d.id
            WHERE fd.film_id = ?;
            """;
    private static final String INSERT_TO_FILM_GENRES_TABLE_QUERY = "INSERT INTO film_genre (film_id, genre_id) VALUES (?, ?)";
    private static final String INSERT_TO_FILM_DIRECTORS_TABLE_QUERY = "INSERT INTO film_directors (film_id, director_id) VALUES (?, ?)";
    private static final String UPDATE_FILM_QUERY = "UPDATE films SET name=?, description=?, release_date=?, duration=?, rating_id=? WHERE id=?";
    private static final String FILMS_COUNT_QUERY = "SELECT COUNT(*) FROM films WHERE id = ?";
    private static final String GET_COMMON_FILMS = """
            SELECT f.*,
                   COUNT(l.film_id) AS like_count
            FROM films f
            LEFT JOIN likes l ON f.id = l.film_id AND l.user_id IN (?, ?)
            GROUP BY f.id
            HAVING COUNT(DISTINCT l.user_id) = 2
            ORDER BY like_count DESC
            """;

    private static final String FIND_SORTED_FILMS_BY_DIRECTORS_ID = """
                SELECT f.*, COUNT(l.film_id) AS like_count
                FROM films f
                JOIN film_directors fd ON f.id = fd.film_id
                LEFT JOIN likes l ON f.id = l.film_id
                WHERE fd.director_id = ?
                GROUP BY f.id, f.name, f.release_date, f.duration, f.description, f.rating_id
            """;
    private static final String FIND_FILMS_BY_DIRECTORS_ID = """
            SELECT * FROM films
            WHERE director_id = ?""";
    private static final String FIND_FILM_ID_BY_NAME_QUERY = "SELECT id FROM films WHERE LOWER(name) LIKE LOWER(?)";
    private static final String FIND_FILM_ID_BY_DIRECTOR_QUERY = "SELECT f.id FROM films AS f LEFT JOIN film_directors AS fd ON fd.film_id = f.id LEFT JOIN directors AS d ON d.id = fd.director_id WHERE LOWER(d.name) LIKE LOWER(?)";
    private static final String FIND_FILM_ID_BY_NAME_AND_DIRECTOR_QUERY = "SELECT f.id FROM films AS f LEFT JOIN film_directors AS fd ON fd.film_id = f.id LEFT JOIN directors AS d ON d.id = fd.director_id WHERE LOWER(f.name) LIKE LOWER(?) OR LOWER(d.name) LIKE LOWER(?) GROUP BY f.id ORDER BY f.id ASC";

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
            thisFilm.ifPresent(this::setDirectorToFilm);
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
        log.debug("Добавление фильма {} в репозитории", film);
        long mpaId = film.getMpa().getId();

        Integer count = jdbc.queryForObject(COUNT_OF_RATINGS_QUERY, Integer.class, mpaId);
        if (count == null || count == 0) {
            throw new NotFoundException("MPA with ID " + mpaId + " wasn't found");
        }

        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbc.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(INSERT_QUERY, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, film.getName());
            ps.setString(2, film.getDescription());
            ps.setDate(3, Date.valueOf(film.getReleaseDate()));
            ps.setInt(4, film.getDuration());
            ps.setLong(5, mpaId);
            return ps;
        }, keyHolder);

        film.setId(keyHolder.getKey().longValue());

        if (film.getGenres() != null && !film.getGenres().isEmpty()) {
            List<Long> genreIds = film.getGenres().stream().map(Genre::getId).toList();

            String inSql = genreIds.stream().map(id -> "?").collect(Collectors.joining(", "));
            List<Long> existingGenreIds = jdbc.queryForList("SELECT id FROM genre WHERE id IN (" + inSql + ")", Long.class, genreIds.toArray());

            for (Long genreId : genreIds) {
                if (!existingGenreIds.contains(genreId)) {
                    throw new NotFoundException("Genre with ID " + genreId + " wasn't found");
                }
            }

            for (Long genreId : genreIds) {
                jdbc.update("INSERT INTO film_genre (film_id, genre_id) VALUES (?, ?)", film.getId(), genreId);
            }
        }

        if (film.getDirectors() != null && !film.getDirectors().isEmpty()) {
            for (Director director : film.getDirectors()) {
                String directorCheckQuery = "SELECT COUNT(*) FROM directors WHERE id = ?";
                Integer directorCount = jdbc.queryForObject(directorCheckQuery, Integer.class, director.getId());
                if (directorCount == null || directorCount == 0) {
                    throw new NotFoundException("Director with ID " + director.getId() + " wasn't found");
                }
                jdbc.update("INSERT INTO film_directors (film_id, director_id) VALUES (?, ?)", film.getId(), director.getId());
            }
        }
        setGenreAndRatingToFilm(film);
        setDirectorToFilm(film);
        log.debug("Фильм {} был добавлен в базу данных", film);
        return film;
    }

    public Film updateFilm(Film film) {
        log.debug("Обновление фильма {} в репозитории", film);

        Integer count = jdbc.queryForObject(FILMS_COUNT_QUERY, Integer.class, film.getId());
        if (count == null || count == 0) {
            throw new NotFoundException("Attempt to update non-existing movie with id " + film.getId());
        }

        jdbc.update(UPDATE_FILM_QUERY, film.getName(), film.getDescription(), Date.valueOf(film.getReleaseDate()), film.getDuration(), film.getMpa().getId(), film.getId());

        jdbc.update("DELETE FROM film_genre WHERE film_id = ?", film.getId());
        if (film.getGenres() != null && !film.getGenres().isEmpty()) {
            for (Genre genre : film.getGenres()) {
                jdbc.update(INSERT_TO_FILM_GENRES_TABLE_QUERY, film.getId(), genre.getId());
            }
        }

        jdbc.update("DELETE FROM film_directors WHERE film_id = ?", film.getId());
        if (film.getDirectors() != null && !film.getDirectors().isEmpty()) {
            for (Director director : film.getDirectors()) {
                jdbc.update(INSERT_TO_FILM_DIRECTORS_TABLE_QUERY, film.getId(), director.getId());
            }
        }

        setGenreAndRatingToFilm(film);
        setDirectorToFilm(film);

        log.debug("Фильм {} был обновлен в базе данных", film);
        return film;
    }

    private void setGenreAndRatingToFilm(Film film) {
        Mpa mpa = jdbc.queryForObject(FIND_MPA_RATINGS_QUERY, (rs, rowNum) -> {
            Mpa mpaObj = new Mpa();
            mpaObj.setId(rs.getLong("id"));
            mpaObj.setName(rs.getString("name"));
            return mpaObj;
        }, film.getMpa().getId());
        film.setMpa(mpa);

        List<Genre> genres = jdbc.query(FIND_GENRES_BY_FILM_QUERY, (rs, rowNum) -> {
            Genre genre = new Genre();
            genre.setId(rs.getInt("id"));
            genre.setName(rs.getString("name"));
            return genre;
        }, film.getId());
        film.setGenres(new LinkedHashSet<>(genres));
    }

    private void setDirectorToFilm(Film film) {
        List<Director> directors = jdbc.query(FIND_DIRECTORS_QUERY, (rs, rowNum) -> {
            Director director = new Director();
            director.setId(rs.getLong("id"));
            director.setName(rs.getString("name"));
            return director;
        }, film.getId());
        film.setDirectors(directors);
    }

    public List<Film> getCommonFilms(Long userId, Long friendId) {
        List<Film> commonFilms = findMany(GET_COMMON_FILMS, userId, friendId);

        for (Film film : commonFilms) {
            Optional<Film> filmWithDetails = getFilmById(film.getId());
            filmWithDetails.ifPresent(updatedFilm -> film.setGenres(updatedFilm.getGenres()));
        }

        return commonFilms;
    }

    public List<Film> findAllById(List<Long> recommendedFilmIds) {
        log.debug("Запрос на список рекомендованных фильмов на основе списка ID  в хранилище фильмов");
        return recommendedFilmIds.stream().map(this::getFilmById).flatMap(Optional::stream).collect(Collectors.toList());
    }

    public Collection<Film> getSortedFilmsByDirector(long directorId, String[] sortParams) {
        Collection<Film> films;

        if (isValidSortParams(sortParams)) {
            String query = FIND_SORTED_FILMS_BY_DIRECTORS_ID + " ORDER BY " + setOrderBy(sortParams);
            films = findMany(query, directorId);
        } else {
            films = findMany(FIND_FILMS_BY_DIRECTORS_ID, directorId);
        }

        for (Film film : films) {
            setDirectorToFilm(film);
        }

        return films;
    }

    private String setOrderBy(String[] sortParams) {
        List<String> sortClauses = new ArrayList<>();

        for (String param : sortParams) {
            switch (param) {
                case "year" -> sortClauses.add("YEAR(f.release_date) ASC");
                case "likes" -> sortClauses.add("like_count DESC");
            }
        }

        return String.join(", ", sortClauses);
    }

    private boolean isValidSortParams(String[] sortParams) {
        Set<String> validParams = Set.of("year", "likes");

        for (String param : sortParams) {
            if (!validParams.contains(param)) {
                return false;
            }
        }
        return true;
    }

    public Collection<Film> searchFilmsByTitle(String query) {
        List<Long> filmsId = jdbc.queryForList(FIND_FILM_ID_BY_NAME_QUERY, Long.class, query);
        return getFilmsById(filmsId);
    }

    public Collection<Film> searchFilmsByDirector(String query) {
        List<Long> filmsId = jdbc.queryForList(FIND_FILM_ID_BY_DIRECTOR_QUERY, Long.class, query);
        return getFilmsById(filmsId);
    }

    public Collection<Film> searchFilmsByTitleAndDirector(String query) {
        List<Long> filmsId = jdbc.queryForList(FIND_FILM_ID_BY_NAME_AND_DIRECTOR_QUERY, Long.class, query, query);
        return getFilmsById(filmsId);
    }

    private Collection<Film> getFilmsById(List<Long> filmsId) {
        List<Film> films = new ArrayList<>();
        for (Long id : filmsId) {
            Optional<Film> optionalFilm = getFilmById(id);
            optionalFilm.ifPresent(films::add);
        }
        films.sort((f1, f2) -> f1.getLikes().size() - f2.getLikes().size());
        return films;
    }
}

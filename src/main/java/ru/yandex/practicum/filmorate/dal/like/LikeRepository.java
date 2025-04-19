package ru.yandex.practicum.filmorate.dal.like;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dal.BaseRepository;
import ru.yandex.practicum.filmorate.dal.film.FilmRowMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Like;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

@Slf4j
@Repository
public class LikeRepository extends BaseRepository<Like> {
    private static final String INSERT_QUERY = "INSERT INTO likes(user_id, film_id) VALUES (?, ?)";
    private static final String DELETE_QUERY = "DELETE FROM likes WHERE user_id = ? AND film_id = ?";
    private static final String COUNT_LIKES_QUERY = "SELECT COUNT(*) FROM likes WHERE film_id = ?";
    private static final String FIND_POPULAR_FILMS_QUERY = """
            SELECT f.id,
                   f.name,
                   f.description,
                   f.release_date,
                   f.duration,
                   f.rating_id,
                   COUNT(fl.user_id) AS likes
            FROM films f
            LEFT JOIN likes fl ON f.id = fl.film_id
            GROUP BY f.id
            ORDER BY likes DESC, f.id ASC LIMIT ?
            """;
    private static final String FIND_POPULAR_FILMS_BY_GENRE_AND_YEAR_QUERY = """
            SELECT f.id,
                   f.name,
                   f.description,
                   f.release_date,
                   f.duration,
                   f.rating_id,
                   COUNT(fl.user_id) AS likes
            FROM films f
            LEFT JOIN likes fl ON f.id = fl.film_id
            LEFT JOIN film_genre AS fg ON fg.film_id = f.id
            WHERE f.release_date >= ? AND f.release_date <= ? AND fg.genre_id = ?
            GROUP BY f.id ORDER BY likes DESC, f.id ASC LIMIT ?
            """;
    private static final String FIND_POPULAR_FILMS_BY_YEAR_QUERY = """
            SELECT f.id,
                   f.name,
                   f.description,
                   f.release_date,
                   f.duration,
                   f.rating_id,
                   COUNT(fl.user_id) AS likes
            FROM films f LEFT JOIN likes fl ON f.id = fl.film_id
            LEFT JOIN film_genre AS fg ON fg.film_id = f.id
            WHERE f.release_date >= ? AND f.release_date <= ?
            GROUP BY f.id ORDER BY likes DESC, f.id ASC LIMIT ?
            """;
    private static final String FIND_POPULAR_FILMS_BY_GENRE_QUERY = """
            SELECT f.id,
                   f.name,
                   f.description,
                   f.release_date,
                   f.duration,
                   f.rating_id,
                   COUNT(fl.user_id) AS likes
            FROM films f
            LEFT JOIN likes fl ON f.id = fl.film_id
            LEFT JOIN film_genre AS fg ON fg.film_id = f.id
            WHERE fg.genre_id = ?
            GROUP BY f.id
            ORDER BY likes DESC, f.id ASC LIMIT ?
            """;
    private static final String FIND_RECOMMENDED_FILMS = """
            SELECT l2.film_id
            FROM likes l1
            JOIN likes l2 ON l1.film_id = l2.film_id
            WHERE l1.user_id = ? AND l2.user_id != ?
              AND l2.film_id NOT IN (
                  SELECT film_id FROM likes WHERE user_id = ?
              )
            GROUP BY l2.film_id
            ORDER BY COUNT(*) DESC
            LIMIT 10
            """;
    private static final String FIND_SIMILAR_USER = """
            SELECT l2.user_id, COUNT(*) AS common_likes
            FROM likes l1
            JOIN likes l2 ON l1.film_id = l2.film_id
            WHERE l1.user_id = ? AND l2.user_id != ?
            GROUP BY l2.user_id
            ORDER BY common_likes DESC
            LIMIT 1
            """;
    private static final String FIND_FILMS_LIKED_BY_SIMILAR_USER = """
            SELECT film_id
            FROM likes
            WHERE user_id = ?
              AND film_id NOT IN (
                SELECT film_id FROM likes WHERE user_id = ?
              )
            LIMIT 10
            """;

    public LikeRepository(JdbcTemplate jdbc, RowMapper<Like> mapper) {
        super(jdbc, mapper);
    }

    public boolean addLike(long userId, long filmId) {
        log.debug("Запрос лайка от пользователя (Id: {}) на фильм (Id: {})", userId, filmId);
        int rowsAffected = jdbc.update(INSERT_QUERY, userId, filmId);
        return rowsAffected > 0;
    }

    public boolean deleteLike(long userId, long filmId) {
        log.debug("Запрос удаления лайка пользователя (Id: {}) с фильма (Id: {})", userId, filmId);
        int rowsAffected = jdbc.update(DELETE_QUERY, userId, filmId);
        return rowsAffected > 0;
    }

    public int getLikeCount(long filmId) {
        log.debug("Запрос на список лайков фильма (Id: {})", filmId);
        return jdbc.queryForObject(COUNT_LIKES_QUERY, Integer.class, filmId);
    }

    public Collection<Film> findPopularFilms(int count) {
        log.debug("Запрос на список {} популярных фильмов", count);
        return jdbc.query(FIND_POPULAR_FILMS_QUERY, new FilmRowMapper(), count);
    }

    public Collection<Film> findPopularFilms(int count, int genreId, int year) {
        log.debug("Запрос на список {} популярных фильмов", count);
        if (genreId == -1) {
            return jdbc.query(FIND_POPULAR_FILMS_BY_YEAR_QUERY,
                    new FilmRowMapper(),
                    String.valueOf(LocalDate.of(year, 1, 1)),
                    String.valueOf(LocalDate.of(year, 12, 31)), count);
        } else if (year == -1) {
            return jdbc.query(FIND_POPULAR_FILMS_BY_GENRE_QUERY, new FilmRowMapper(), genreId, count);
        } else {
            return jdbc.query(FIND_POPULAR_FILMS_BY_GENRE_AND_YEAR_QUERY,
                    new FilmRowMapper(),
                    String.valueOf(LocalDate.of(year, 1, 1)),
                    String.valueOf(LocalDate.of(year, 12, 31)), genreId, count);
        }
    }

    public boolean isThisPairExist(long userId, long filmId) {
        log.debug("Запрос на проверку наличия лайка от пользователя (Id: {}) у фильма (Id: {})", userId, filmId);
        String checkQuery = "SELECT COUNT(*) FROM likes WHERE user_id = ? AND film_id = ?";
        int count = jdbc.queryForObject(checkQuery, Integer.class, userId, filmId);
        return count > 0;
    }

    public List<Long> findRecommendedFilmIds(Long userId) {
        log.debug("Запрос на список ID рекомендованных фильмов для пользователя (Id: {}) в хранилище", userId);
        Long similarUserId = findMostSimilarUser(userId);
        if (similarUserId == null) {
            log.debug("Похожих пользователей не найдено для пользователя {}", userId);
            return List.of();
        }

        log.debug("Похожий пользователь найден: {}", similarUserId);
        return findRecommendedFilmIdsBySimilarUser(similarUserId, userId);
    }

    private Long findMostSimilarUser(Long userId) {
        List<Long> result = jdbc.query(
                FIND_SIMILAR_USER,
                (rs, rowNum) -> rs.getLong("user_id"),
                userId, userId
        );
        return result.isEmpty() ? null : result.getFirst();
    }

    private List<Long> findRecommendedFilmIdsBySimilarUser(Long similarUserId, Long userId) {
        return jdbc.query(
                FIND_FILMS_LIKED_BY_SIMILAR_USER,
                (rs, rowNum) -> rs.getLong("film_id"),
                similarUserId, userId
        );
    }
}
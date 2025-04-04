package ru.yandex.practicum.filmorate.dal.like;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dal.BaseRepository;
import ru.yandex.practicum.filmorate.dal.film.FilmRowMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Like;

import java.util.Collection;
import java.util.List;

@Repository
public class LikeRepository extends BaseRepository<Like> {
    private static final String FIND_LIKED_FILMS_BY_USER_ID_QUERY = "SELECT f.name FROM films f JOIN likes l ON f.id = l.film.id WHERE l.user_id = ?";
    private static final String INSERT_QUERY = "INSERT INTO likes(user_id, film_id) VALUES (?, ?)";
    private static final String DELETE_QUERY = "DELETE FROM likes WHERE user_id = ? AND film_id = ?";
    private static final String COUNT_LIKES_QUERY = "SELECT COUNT(*) FROM likes WHERE film_id = ?";
    private static final String FIND_POPULAR_FILMS_QUERY =
            "SELECT f.id, f.name, f.description, f.release_date, f.duration, f.rating_id, COUNT(fl.user_id) AS likes " +
                    "FROM films f LEFT JOIN likes fl ON f.id = fl.film_id GROUP BY f.id ORDER BY likes DESC, f.id ASC LIMIT ?";

    public LikeRepository(JdbcTemplate jdbc, RowMapper<Like> mapper) {
        super(jdbc, mapper);
    }

    public boolean addLike(long userId, long filmId) {
        int rowsAffected = jdbc.update(INSERT_QUERY, userId, filmId);
        return rowsAffected > 0;
    }

    public boolean deleteLike(long userId, long filmId) {
        int rowsAffected = jdbc.update(DELETE_QUERY, userId, filmId);
        return rowsAffected > 0;
    }

    public int getLikeCount(long filmId) {
        return jdbc.queryForObject(COUNT_LIKES_QUERY, Integer.class, filmId);
    }

    public List<Film> findLikedFilmByUserId(long userId) {
        return jdbc.query(FIND_LIKED_FILMS_BY_USER_ID_QUERY, new FilmRowMapper(), userId);
    }

    public Collection<Film> findPopularFilms(int count) {
        return jdbc.query(FIND_POPULAR_FILMS_QUERY, new FilmRowMapper(), count);
    }

    public boolean isThisPairExist(long userId, long filmId) {
        String checkQuery = "SELECT COUNT(*) FROM likes WHERE user_id = ? AND film_id = ?";
        int count = jdbc.queryForObject(checkQuery, Integer.class, userId, filmId);
        return count > 0;
    }
}

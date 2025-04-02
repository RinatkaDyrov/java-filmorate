package ru.yandex.practicum.filmorate.dal.like;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dal.BaseRepository;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Like;

import java.util.List;

@Repository
public class LikeRepository extends BaseRepository {
    private static final String FIND_ALL_QUERY = "SELECT * FROM likes";
    private static final String FIND_LIKED_FILMS_BY_USER_ID_QUERY = "SELECT f.name FROM films f JOIN likes l ON f.id = l.film.id WHERE l.user_id = ?";
    private static final String INSERT_QUERY = "INSERT INTO likes(user_id, film_id) VALUES (?, ?)  ON CONFLICT DO NOTHING";
    private static final String DELETE_QUERY = "DELETE FROM likes WHERE user_id = ? AND film_id = ?";
    private static final String COUNT_LIKES_QUERY = "SELECT COUNT(*) FROM likes WHERE film_id = ?";

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

    public int getLikeCount(long filmId){
        return jdbc.queryForObject(COUNT_LIKES_QUERY, Integer.class, filmId);
    }

    public List<Film> findLikedFilmByUserId(long userId){
        return jdbc.queryForList(FIND_LIKED_FILMS_BY_USER_ID_QUERY, Film.class, userId);
    }
}

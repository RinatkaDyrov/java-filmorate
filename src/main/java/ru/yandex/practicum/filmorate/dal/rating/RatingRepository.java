package ru.yandex.practicum.filmorate.dal.rating;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dal.BaseRepository;
import ru.yandex.practicum.filmorate.dal.film.FilmRowMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.RatingMBA;

import java.util.List;

@Repository
public class RatingRepository extends BaseRepository {

    private static final String INSERT_QUERY = "INSERT INTO rating_mba(name) VALUES (?) returning id";
    private static final String FIND_FILMS_BY_RATING_QUERY = "SELECT f.* FROM films f JOIN rating_mba r ON f.rating_id = r.id WHERE r.name = ?";

    public RatingRepository(JdbcTemplate jdbc, RowMapper mapper) {
        super(jdbc, mapper);
    }

    public List<Film> getFilmsByRating(String ratingName) {
        return jdbc.query(FIND_FILMS_BY_RATING_QUERY, new FilmRowMapper(), ratingName);
    }

    public RatingMBA save(RatingMBA rating) {
        int id = (int) insert(INSERT_QUERY, rating.getName());
        rating.setId(id);
        return rating;
    }
}

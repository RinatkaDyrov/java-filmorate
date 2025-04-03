package ru.yandex.practicum.filmorate.dal.rating;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dal.BaseRepository;
import ru.yandex.practicum.filmorate.dal.film.FilmRowMapper;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.Collection;
import java.util.List;

@Slf4j
@Repository
public class MpaRepository extends BaseRepository<Mpa> {

    private static final String INSERT_QUERY = "INSERT INTO rating_mpa(name) VALUES (?)";
    private static final String FIND_ALL_QUERY = "SELECT * FROM rating_mpa";
    private static final String FIND_BY_ID_QUERY = "SELECT * FROM rating_mpa WHERE id = ?";
    private static final String FIND_FILMS_BY_RATING_QUERY = "SELECT f.* FROM films f JOIN rating_mpa r ON f.rating_id = r.id WHERE r.name = ?";

    public MpaRepository(JdbcTemplate jdbc, RowMapper<Mpa> mapper) {
        super(jdbc, mapper);
    }

    public List<Film> getFilmsByRating(String ratingName) {
        return jdbc.query(FIND_FILMS_BY_RATING_QUERY, new FilmRowMapper(), ratingName);
    }

    public Mpa save(Mpa rating) {
        long id = insert(INSERT_QUERY, rating.getName());
        rating.setId(id);
        return rating;
    }

    public Collection<Mpa> findAll() {
        return findMany(FIND_ALL_QUERY);
    }

    public Mpa findById(long id) {
        log.info("Ищем рейтинг в репозитории по id {}", id);
        return findOne(FIND_BY_ID_QUERY, id)
                .orElseThrow(() -> new NotFoundException("Данного рейтинга нет в списке"));
    }

}

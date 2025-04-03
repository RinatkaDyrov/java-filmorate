package ru.yandex.practicum.filmorate.dal.genre;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dal.BaseRepository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;

@Repository
public class GenreRepository extends BaseRepository<Genre> {

    private static final String FIND_ALL_QUERY = "SELECT * FROM genre";
    private static final String FIND_BY_ID_QUERY = "SELECT * FROM genre WHERE id = ?";
    private static final String INSERT_QUERY = "INSERT INTO genre(name) VALUES (?)";
    private static final String UPDATE_QUERY = "UPDATE genre SET name = ? WHERE id = ?";

    public GenreRepository(JdbcTemplate jdbc, RowMapper<Genre> mapper) {
        super(jdbc, mapper);
    }

    public List<Genre> findAllGenres() {
        return findMany(FIND_ALL_QUERY);
    }

    public Genre findGenreById(long id) {
        return findOne(FIND_BY_ID_QUERY, id)
                .orElseThrow(() -> new NotFoundException("Данного жанра нет в списке"));
    }

    public Genre save(Genre genre) {
        int id = (int) insert(INSERT_QUERY, genre.getName());
        genre.setId(id);
        return genre;
    }

    public Genre update(Genre genre) {
        update(UPDATE_QUERY, genre.getName(), genre.getId());
        return genre;
    }
}

package ru.yandex.practicum.filmorate.dal.director;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dal.BaseRepository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Director;

import java.util.Collection;
import java.util.Optional;

@Slf4j
@Repository
public class DirectorRepository extends BaseRepository<Director> {
    private static final String FIND_ALL_DIRECTORS_QUERY = "SELECT * FROM directors ORDER BY id ASC";
    private static final String FIND_BY_ID_QUERY = "SELECT * FROM directors WHERE id = ?";
    private static final String INSERT_QUERY = "INSERT INTO directors (name) VALUES (?)";
    private static final String UPDATE_QUERY = "UPDATE directors SET name = ? WHERE id = ?";
    private static final String DELETE_QUERY = "DELETE FROM directors WHERE id = ?";

    public DirectorRepository(JdbcTemplate jdbc, RowMapper<Director> mapper) {
        super(jdbc, mapper);
    }


    public Collection<Director> findAllDirectors() {
        return findMany(FIND_ALL_DIRECTORS_QUERY);
    }

    public Optional<Director> findDirectorById(long id) {
        try {
            return findOne(FIND_BY_ID_QUERY, id);
        } catch (EmptyResultDataAccessException e) {
            log.warn("Не найден режиссер (ID: {})", id);
            throw new NotFoundException("Не найден режиссер (ID: {" + id + "})");
        }
    }

    public Director save(Director director) {
        log.debug("Запрос на добавления режиссера ({}) в базу данных", director);
        long id = insert(INSERT_QUERY, director.getName());
        director.setId(id);
        return director;
    }

    public Director updateDirector(Director updDirector) {
        log.debug("Запрос на обновление режиссера ({}) в базе данных", updDirector);
        update(UPDATE_QUERY, updDirector.getName(), updDirector.getId());
        return updDirector;
    }

    public void delete(long id) {
        log.debug("Запрос на удаление режиссера (ID: {}) из базы данных", id);
        delete(DELETE_QUERY, id);
    }
}

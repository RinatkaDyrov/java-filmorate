package ru.yandex.practicum.filmorate.dal.director;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import ru.yandex.practicum.filmorate.dal.BaseRepository;

public class DirectorRepository extends BaseRepository {
    public DirectorRepository(JdbcTemplate jdbc, RowMapper mapper) {
        super(jdbc, mapper);
    }


}

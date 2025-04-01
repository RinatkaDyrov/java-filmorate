package ru.yandex.practicum.filmorate.dal.like;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dal.BaseRepository;

@Repository
public class LikeRepository extends BaseRepository {
    private static final String FIND_ALL_QUERY = "SELECT * FROM likes";
    private static final String FIND_BY_ID_QUERY = "SELECT * FROM likes WHERE id = ?";
    private static final String INSERT_QUERY = "INSERT INTO likes(film) VALUES (?) returning id";
    private static final String UPDATE_QUERY = "UPDATE likes SET name = ? WHERE id = ?";
    private static final String COUNT_LIKES_QUERY = "SELECT COUNT(*) FROM likes WHERE id = ?";

    public LikeRepository(JdbcTemplate jdbc, RowMapper mapper) {
        super(jdbc, mapper);
    }


}

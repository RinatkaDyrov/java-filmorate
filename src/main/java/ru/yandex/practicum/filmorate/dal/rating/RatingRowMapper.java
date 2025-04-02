package ru.yandex.practicum.filmorate.dal.rating;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.RatingMBA;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class RatingRowMapper implements RowMapper<RatingMBA> {
    @Override
    public RatingMBA mapRow(ResultSet resultSet, int rowNum) throws SQLException {
        RatingMBA rating = new RatingMBA();
        rating.setId(resultSet.getInt("id"));
        rating.setName(resultSet.getString("name"));
        return rating;
    }
}

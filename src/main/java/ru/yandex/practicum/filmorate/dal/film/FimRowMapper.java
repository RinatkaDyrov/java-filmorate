package ru.yandex.practicum.filmorate.dal.film;

import org.springframework.jdbc.core.RowMapper;
import ru.yandex.practicum.filmorate.model.Film;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;

public class FimRowMapper implements RowMapper {

    @Override
    public Object mapRow(ResultSet resultSet, int rowNum) throws SQLException {
        Film film = new Film();
        film.setId(resultSet.getLong("id"));
        film.setName(resultSet.getString("name"));
        film.setDescription(resultSet.getString("description"));

        Date releaseDate = resultSet.getDate("releaseDate");
        film.setReleaseDate(releaseDate.toLocalDate());

        film.setDuration(resultSet.getInt("duration"));

        return film;
    }
}

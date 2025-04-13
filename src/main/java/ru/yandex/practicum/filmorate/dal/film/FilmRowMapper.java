package ru.yandex.practicum.filmorate.dal.film;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.io.Serializable;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class FilmRowMapper implements RowMapper<Film>, Serializable {

    @Override
    public Film mapRow(ResultSet resultSet, int rowNum) throws SQLException {
        Film film = new Film();
        film.setId(resultSet.getLong("id"));
        film.setName(resultSet.getString("name"));
        film.setDescription(resultSet.getString("description"));

        Date releaseDate = resultSet.getDate("release_date");
        if (releaseDate != null) {
            film.setReleaseDate(releaseDate.toLocalDate());
        } else {
            film.setReleaseDate(null);
        }

        film.setDuration(resultSet.getInt("duration"));

        Mpa mpa = new Mpa();
        mpa.setId(resultSet.getLong("rating_id"));
        film.setMpa(mpa);

//        Director director = new Director();
//        director.setId(resultSet.getLong("director_id"));
//        film.setDirector(director);

        return film;
    }


}

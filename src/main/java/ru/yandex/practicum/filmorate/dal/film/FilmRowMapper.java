package ru.yandex.practicum.filmorate.dal.film;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Component
public class FilmRowMapper implements RowMapper<Film> {

    @Override
    public Film mapRow(ResultSet resultSet, int rowNum) throws SQLException {
        Film film = new Film();
        film.setId(resultSet.getLong("id"));
        film.setName(resultSet.getString("name"));
        film.setDescription(resultSet.getString("description"));

        // Обработка даты
        Date releaseDate = resultSet.getDate("release_date");
        if (releaseDate != null) {
            film.setReleaseDate(releaseDate.toLocalDate());
        } else {
            film.setReleaseDate(null);  // Или какое-то дефолтное значение, если необходимо
        }

        film.setDuration(resultSet.getInt("duration"));

        // Обработка жанров
        List<Genre> genres = new ArrayList<>();
        do {
            if (resultSet.getInt("genre_id") != 0) {  // Если жанр существует (не 0)
                Genre genre = new Genre();
                genre.setId(resultSet.getInt("genre_id"));
                genre.setName(resultSet.getString("genre_name"));
                genres.add(genre);
            }
        } while (resultSet.next() && resultSet.getLong("id") == film.getId());  // Продолжаем до тех пор, пока не обработаем все строки для этого фильма

        film.setGenres(genres);  // Множество жанров (или пустой список, если нет жанров)

        // Обработка рейтинга (один рейтинг для фильма)
        Mpa rating = null;
        if (resultSet.getInt("rating_id") != 0) {  // Если рейтинг существует (не 0)
            rating = new Mpa();
            rating.setId(resultSet.getInt("rating_id"));
            rating.setName(resultSet.getString("rating_name"));
        }
        film.setMpa(rating);  // Один рейтинг или null, если нет

        return film;
    }


}

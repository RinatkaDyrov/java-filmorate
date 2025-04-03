package ru.yandex.practicum.filmorate.dto.film;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.time.LocalDate;
import java.time.Month;
import java.util.List;
import java.util.Set;

@Data
public class UpdateFilmRequest {
    @NotNull(message = "Необходимо указать название")
    @NotBlank(message = "Название не может быть пустым")
    private String name;
    @Size(max = 200, message = "Максимальная длина описания — 200 символов")
    private String description;
    private LocalDate releaseDate;
    @Positive(message = "Продолжительность фильма должна быть положительным числом")
    private int duration;
    private Set<Genre> genres;
    private Mpa rate;

    public boolean hasName() {
        return ! (name == null || name.isBlank());
    }

    public boolean hasDescription() {
        return ! (description == null || description.isBlank());
    }

    public boolean hasReleaseDate() {
        return ! (releaseDate == null || releaseDate.isAfter(LocalDate.of(1895, Month.DECEMBER, 28)));
    }

    public boolean hasDuration() {
        return ! (duration <= 0);
    }

    public boolean hasGenres() {
        return ! (genres == null || genres.isEmpty());
    }
    public boolean hasMpa() {
        return ! (rate == null);
    }
}

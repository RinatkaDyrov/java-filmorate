package ru.yandex.practicum.filmorate.dto.film;

import lombok.Data;

import java.time.LocalDate;

@Data
public class UpdateFilmRequest {
    private String name;
    private String description;
    private LocalDate releaseDate;
    private int duration;

    public boolean hasName() {
        return ! (name == null || name.isBlank());
    }

    public boolean hasDescription() {
        return ! (description == null || description.isBlank());
    }

    public boolean hasReleaseDate() {
        return ! (releaseDate == null || releaseDate.isAfter(LocalDate.now()));
    }

    public boolean hasDuration() {
        return ! (duration <= 0);
    }
}

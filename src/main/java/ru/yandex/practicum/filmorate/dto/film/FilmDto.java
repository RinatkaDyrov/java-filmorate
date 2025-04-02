package ru.yandex.practicum.filmorate.dto.film;

import lombok.Data;

import java.time.LocalDate;

@Data
public class FilmDto {
    private Long id;
    private String name;
    private String description;
    private LocalDate releaseDate;
    private int duration;
}

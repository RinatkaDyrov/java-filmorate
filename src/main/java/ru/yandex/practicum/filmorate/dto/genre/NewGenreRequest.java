package ru.yandex.practicum.filmorate.dto.genre;

import lombok.Data;

@Data
public class NewGenreRequest {
    private long id;
    private String name;
}

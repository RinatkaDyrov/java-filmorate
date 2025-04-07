package ru.yandex.practicum.filmorate.model;

import lombok.Data;

@Data
public class Like {
    private long userId;
    private long filmId;
}

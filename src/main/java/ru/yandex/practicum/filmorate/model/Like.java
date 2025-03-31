package ru.yandex.practicum.filmorate.model;

import lombok.Data;

@Data
public class Like {
    private long user_id;
    private long film_id;
}

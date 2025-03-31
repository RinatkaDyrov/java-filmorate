package ru.yandex.practicum.filmorate.model;

import lombok.Data;

@Data
public class Friendship {
    private long user_id;
    private long friend_id;
    private boolean confirmStatus;
}

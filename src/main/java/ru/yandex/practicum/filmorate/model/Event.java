package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import ru.yandex.practicum.filmorate.model.event.EventType;
import ru.yandex.practicum.filmorate.model.event.OperationType;

@Data
public class Event {
    private Long timestamp;
    private long userId;
    private EventType eventType;
    private OperationType operation;
    private Long eventId;
    private Long entityId;
}

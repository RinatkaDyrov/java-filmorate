package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dal.event.EventRepository;
import ru.yandex.practicum.filmorate.model.Event;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class EventStorage {

    private final EventRepository eventRepository;

    public List<Event> getEventListByUserId(long id) {
        return eventRepository.getEventListByUserId(id);
    }
}

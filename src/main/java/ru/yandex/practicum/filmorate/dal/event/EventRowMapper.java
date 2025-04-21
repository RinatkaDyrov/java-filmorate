package ru.yandex.practicum.filmorate.dal.event;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.event.EventType;
import ru.yandex.practicum.filmorate.model.event.OperationType;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class EventRowMapper implements RowMapper<Event> {

    @Override
    public Event mapRow(ResultSet resultSet, int rowNum) throws SQLException {
        Event event = new Event();
        event.setTimestamp(resultSet.getLong("time_stamp"));
        event.setUserId(resultSet.getLong("user_id"));
        event.setEventType(EventType.valueOf(resultSet.getString("event_type").toUpperCase()));
        event.setOperation(OperationType.valueOf(resultSet.getString("operation").toUpperCase()));
        event.setEventId(resultSet.getLong("event_id"));
        event.setEntityId(resultSet.getLong("entity_id"));
        return event;
    }
}

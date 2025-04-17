package ru.yandex.practicum.filmorate.dal.event;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dal.BaseRepository;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.event.EventType;
import ru.yandex.practicum.filmorate.model.event.OperationType;

import java.time.Instant;
import java.util.List;

@Slf4j
@Repository
public class EventRepository extends BaseRepository<Event> {

    private static final String FIND_EVENT_LIST_BY_USER_ID = "SELECT * FROM events WHERE user_id=?";
    private static final String INSERT_NEW_EVENT = """
            INSERT INTO events (time_stamp, user_id, event_type, operation, entity_id)
            VALUES (?, ?, ?, ?, ?)""";

    public EventRepository(JdbcTemplate jdbc, RowMapper<Event> mapper) {
        super(jdbc, mapper);
    }

    public List<Event> getEventListByUserId(long id) {
        return findMany(FIND_EVENT_LIST_BY_USER_ID, id);
    }

    public void addLikeEvent(long userId, long filmId) {
        log.info("Запись события addLikeEvent");
        jdbc.update(INSERT_NEW_EVENT,
                Instant.now().toEpochMilli(),
                userId,
                EventType.LIKE.toString(),
                OperationType.ADD.toString(),
                filmId);
    }

    public void removeLikeEvent(long userId, long filmId) {
        log.info("Запись события removeLikeEvent");

        jdbc.update(INSERT_NEW_EVENT,
                Instant.now().toEpochMilli(),
                userId,
                EventType.LIKE.toString(),
                OperationType.REMOVE.toString(),
                filmId);
    }

    public void removeFriendEvent(long userId, long friendId) {
        log.info("Запись события removeFriendEvent");
        jdbc.update(INSERT_NEW_EVENT,
                Instant.now().toEpochMilli(),
                userId,
                EventType.FRIEND.toString(),
                OperationType.REMOVE.toString(),
                friendId);
    }

    public void addFriendEvent(long userId, long friendId) {
        log.info("Запись события addFriendEvent");
        jdbc.update(INSERT_NEW_EVENT,
                Instant.now().toEpochMilli(),
                userId,
                EventType.FRIEND.toString(),
                OperationType.ADD.toString(),
                friendId);
    }

    public void addReviewEvent(Long userId, Long reviewId) {
        log.info("Запись события addReviewEvent");
        jdbc.update(INSERT_NEW_EVENT,
                Instant.now().toEpochMilli(),
                userId,
                EventType.REVIEW.toString(),
                OperationType.ADD.toString(),
                reviewId);
    }

    public void updateReviewEvent(Long userId, Long reviewId) {
        log.info("Запись события updateReviewEvent");
        jdbc.update(INSERT_NEW_EVENT,
                Instant.now().toEpochMilli(),
                userId,
                EventType.REVIEW.toString(),
                OperationType.UPDATE.toString(),
                reviewId);
    }

    public void removeReviewEvent(Long userId, Long reviewId) {
        log.info("Запись removeReviewEvent addReviewEvent");
        jdbc.update(INSERT_NEW_EVENT,
                Instant.now().toEpochMilli(),
                userId,
                EventType.REVIEW.toString(),
                OperationType.REMOVE.toString(),
                reviewId);
    }
}

package ru.yandex.practicum.filmorate.dal.friendship;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dal.BaseRepository;
import ru.yandex.practicum.filmorate.dal.user.UserRowMapper;
import ru.yandex.practicum.filmorate.model.Friendship;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.List;
import java.util.Map;

@Slf4j
@Repository
public class FriendshipRepository extends BaseRepository<Friendship> {
    private static final String FIND_FRIENDS_BY_USER_QUERY = "SELECT u.* FROM users u JOIN friendship f ON u.id = f.friend_id WHERE f.user_id = ?";
    private static final String FIND_FRIENDS_COUNT_BY_USER_QUERY = "SELECT COUNT(*) FROM friendship WHERE user_id = ? AND confirm_status = TRUE";
    private static final String INSERT_QUERY_FOR_ADD_FRIEND = "INSERT INTO friendship (user_id, friend_id, confirm_status) VALUES (?, ?, FALSE)";
    private static final String UPDATE_QUERY_TO_CONFIRM_FRIEND = "INSERT INTO friendship (user_id, friend_id, confirm_status) VALUES (?, ?, TRUE)";
    private static final String DELETE_QUERY = "DELETE FROM friendship WHERE (user_id = ? AND friend_id = ?)";
    private static final String FIND_COMMON_FRIENDS_QUERY = "SELECT u.* " +
            "FROM friendship f1 JOIN friendship f2 ON f1.friend_id = f2.friend_id JOIN users u ON u.id = f1.friend_id " +
            "WHERE f1.user_id = ? AND f2.user_id = ?";


    public FriendshipRepository(JdbcTemplate jdbc, RowMapper<Friendship> mapper) {
        super(jdbc, mapper);
    }

    public List<User> findFriendsByUserId(long userId) {
        return jdbc.query(FIND_FRIENDS_BY_USER_QUERY, new UserRowMapper(), userId);
    }

    public int countFriendsByUserId(long userId) {
        return jdbc.queryForObject(FIND_FRIENDS_COUNT_BY_USER_QUERY, Integer.class, userId);
    }

    public boolean addFriendRequest(long userId, long friendId) {
        log.debug("Добавляем в друзья {} и {}", userId, friendId);
        try {
            String checkQuery = "SELECT * FROM friendship WHERE user_id = ? AND friend_id = ? AND confirm_status = TRUE";
            List<Map<String, Object>> result = jdbc.queryForList(checkQuery, friendId, userId);

            if (!result.isEmpty()) {
                return true;
            }
            int rowsAffected = jdbc.update(INSERT_QUERY_FOR_ADD_FRIEND, userId, friendId);
            return rowsAffected > 0;
        } catch (Exception e) {
            log.error("Ошибка при добавлении запроса в друзья", e);
            return false;
        }
    }

    public boolean confirmFriendship(long userId, long friendId) {
        int rowsAffected = jdbc.update(UPDATE_QUERY_TO_CONFIRM_FRIEND, userId, friendId);
        return rowsAffected > 0;
    }

    public boolean removeFriend(long userId, long friendId) {
        return delete(DELETE_QUERY, userId, friendId);
    }

    public Collection<User> findCommonFriends(long userId, long friendId) {
        return jdbc.query(FIND_COMMON_FRIENDS_QUERY, new UserRowMapper(), userId, friendId);
    }
}
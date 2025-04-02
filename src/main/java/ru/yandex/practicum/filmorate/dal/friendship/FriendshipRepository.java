package ru.yandex.practicum.filmorate.dal.friendship;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dal.BaseRepository;
import ru.yandex.practicum.filmorate.dal.user.UserRowMapper;
import ru.yandex.practicum.filmorate.model.Friendship;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

@Repository
public class FriendshipRepository extends BaseRepository {
    private final String FIND_FRIENDS_BY_USER_QUERY = "SELECT u.* FROM users u JOIN friendship f ON u.id = f.friend_id WHERE user_id = ? AND confirm_status = TRUE";
    private final String FIND_FRIENDS_COUNT_BY_USER_QUERY = "SELECT COUNT(*) FROM friendships WHERE user_id = ? AND confirm_status = TRUE";
    private final String INSERT_QUERY_FOR_ADD_FRIEND = "INSERT INTO friendships (user_id, friend_id, confirm_status) VALUES (?, ?, FALSE)";
    private final String UPDATE_QUERY_TO_CONFIRM_FRIEND = "UPDATE friendships SET confirm_status = TRUE WHERE user_id = ? AND friend_id = ?";
    private final String DELETE_QUERY = "DELETE FROM friendships WHERE (user_id = ? AND friend_id = ?) OR (user_id = ? AND friend_id = ?)";


    public FriendshipRepository(JdbcTemplate jdbc, RowMapper<Friendship> mapper) {
        super(jdbc, mapper);
    }

    public List<User> findFriendsByUserId(long userId) {
        return jdbc.query(FIND_FRIENDS_BY_USER_QUERY, new UserRowMapper(), userId);
    }

    public int countFriendsByUserId(long userId) {
        return jdbc.queryForObject(FIND_FRIENDS_COUNT_BY_USER_QUERY, Integer.class, userId);
    }

    public void addFriendRequest(long userId, long friendId) {
        insert(INSERT_QUERY_FOR_ADD_FRIEND, userId, friendId);
    }

    public void confirmFriendship(long userId, long friendId) {
        update(UPDATE_QUERY_TO_CONFIRM_FRIEND, userId, friendId);
    }

    public boolean removeFriend(long userId, long friendId) {
        return delete(DELETE_QUERY, userId, friendId, friendId, userId);
    }
}
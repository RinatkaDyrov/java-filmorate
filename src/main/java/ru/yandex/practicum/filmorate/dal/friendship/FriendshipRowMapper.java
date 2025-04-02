package ru.yandex.practicum.filmorate.dal.friendship;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Friendship;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class FriendshipRowMapper implements RowMapper<Friendship> {
    @Override
    public Friendship mapRow(ResultSet resultSet, int rowNum) throws SQLException {
        Friendship friendship = new Friendship();
        friendship.setUser_id(resultSet.getLong("id"));
        friendship.setFriend_id(resultSet.getLong("friend_id"));
        friendship.setConfirmStatus(resultSet.getBoolean("confirm_status"));
        return friendship;
    }
}
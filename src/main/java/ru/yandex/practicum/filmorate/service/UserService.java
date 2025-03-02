package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Collection;

@Slf4j
@Service
public class UserService {
    private final UserStorage userStorage;

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public void addFriend(long userId, long friendId){
        if (userId == friendId){
            log.warn("Ошибка идентификационного номера при добавлении в друзья:" +
                    " userId: {}, friendId: {}", userId, friendId);
            throw new ValidationException("Нельзя добавить самого себя в друзья.");
        }
        User user = userStorage.findUserById(userId);
        User friend = userStorage.findUserById(friendId);

        if (user.getFriends().containsKey(friendId)) {
            throw new ValidationException("Пользователи " + user.getName() + " и " + friend.getName() + " уже друзья");
        }

        user.getFriends().put(friendId, userStorage.findUserById(friendId));
        friend.getFriends().put(userId, userStorage.findUserById(userId));

        userStorage.update(user);
        userStorage.update(friend);
    }

    public void deleteFriend(long userId, long friendId){
        if (userId == friendId){
            log.warn("Ошибка идентификационного номера при удалении из друзей:" +
                    " userId: {}, friendId: {}", userId, friendId);
            throw new ValidationException("Нельзя удалить самого себя из друзей.");
        }
        User user = userStorage.findUserById(userId);
        User friend = userStorage.findUserById(friendId);

        if (!user.getFriends().containsKey(friendId)) {
            throw new ValidationException("Пользователи " + user.getName() + " и " + friend.getName() + " не дружат");
        }

        user.getFriends().remove(friendId);
        friend.getFriends().remove(userId);

        userStorage.update(user);
        userStorage.update(friend);
    }

    public Collection<User> getFriendByUserId(long id){
        return userStorage.findUserById(id).getFriends().values();
    }
}

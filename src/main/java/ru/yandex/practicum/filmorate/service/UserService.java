package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dal.friendship.FriendshipRepository;
import ru.yandex.practicum.filmorate.dal.user.UserRepository;
import ru.yandex.practicum.filmorate.dto.user.NewUserRequest;
import ru.yandex.practicum.filmorate.dto.user.UpdateUserRequest;
import ru.yandex.practicum.filmorate.dto.user.UserDto;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.mapper.UserMapper;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Collection;
import java.util.stream.Collectors;

@Slf4j
@Service
public class UserService {
    private final UserStorage userStorage;

    @Autowired
    public UserService(@Qualifier("userDbStorage") UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public UserDto createUser(NewUserRequest request) {
        if (request.getEmail() == null || request.getEmail().isEmpty()){
            throw new ConditionsNotMetException("Имейл должен быть указан");
        }
        userStorage.findUserByEmail(request.getEmail());

        User user = UserMapper.mapToUser(request);
        user = userStorage.create(user);
        return UserMapper.mapToUserDto(user);
    }

    public UserDto updateUser(long id, UpdateUserRequest request) {
        User updUser = userStorage.findUserById(id);
        updUser = UserMapper.updateUserFields(updUser, request);
        updUser = userStorage.update(updUser);
        return UserMapper.mapToUserDto(updUser);
    }

    public Collection<UserDto> getAllUsers() {
        return userStorage.findAll()
                .stream()
                .map(UserMapper::mapToUserDto)
                .collect(Collectors.toList());
    }

    public UserDto getUserById(long id) {
        User user = userStorage.findUserById(id);
        return UserMapper.mapToUserDto(user);
    }

    public void addFriend(long userId, long friendId) {
        if (userId == friendId) {
            log.warn("Ошибка идентификационного номера при добавлении в друзья:" +
                    " userId: {}, friendId: {}", userId, friendId);
            throw new ValidationException("Нельзя добавить самого себя в друзья.");
        }
        User user = userStorage.findUserById(userId);
        User friend = userStorage.findUserById(friendId);

        if (user == null || friend == null) {
            log.warn("Ошибка: один из пользователей не найден. userId: {}, friendId: {}", userId, friendId);
            throw new NotFoundException("Один из пользователей не существует.");
        }

        boolean success = userStorage.addFriend(userId, friendId);
        if (success) {
            log.info("Пользователь {} отправил заявку на дружбу пользователю {}", user.getName(), friend.getName());
        } else {
            log.warn("Ошибка при добавлении друга. userId: {}, friendId: {}", userId, friendId);
            throw new RuntimeException("Не удалось добавить друга.");
        }
    }

    public void deleteFriend(long userId, long friendId) {
        if (userId == friendId) {
            log.warn("Ошибка идентификационного номера при удалении из друзей:" +
                    " userId: {}, friendId: {}", userId, friendId);
            throw new ValidationException("Нельзя удалить самого себя из друзей.");
        }
        User user = userStorage.findUserById(userId);
        User friend = userStorage.findUserById(friendId);

        if (user == null || friend == null) {
            log.warn("Ошибка: один из пользователей не найден. userId: {}, friendId: {}", userId, friendId);
            throw new NotFoundException("Один из пользователей не существует.");
        }

        boolean success = userStorage.removeFriend(userId, friendId);
        if (success) {
            log.info("Пользователь {} удалил пользователя {} из друзей", user.getName(), friend.getName());
        } else {
            log.warn("Ошибка при удалении друга. userId: {}, friendId: {}", userId, friendId);
            throw new RuntimeException("Не удалось удалить пользователя из друзей.");
        }
    }

    public Collection<User> getFriendByUserId(long id) {
        log.debug("Получение списка друзей пользователя (ID: {})", id);
        return userStorage.getFriends(id);
    }

    public Collection<User> getCommonFriends(long userId, long friendId) {
        log.debug("Получение списка общих друзей пользователей (ID: {}) и (ID: {})", userId, friendId);
        return userStorage.getCommonFriends(userId, friendId);
    }
}

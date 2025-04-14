package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.dto.user.NewUserRequest;
import ru.yandex.practicum.filmorate.dto.user.UpdateUserRequest;
import ru.yandex.practicum.filmorate.dto.user.UserDto;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.mapper.UserMapper;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.interfaces.UserStorage;

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
        if (request.getEmail() == null || request.getEmail().isEmpty()) {
            throw new ConditionsNotMetException("Имейл должен быть указан");
        }
        try {
            userStorage.findUserByEmail(request.getEmail());
            throw new ConditionsNotMetException("Пользователь с таким email уже существует");
        } catch (NotFoundException e) {
            log.info("Создание пользователя");
        }

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

    // Унифицированный метод
    public UserDto getUserById(long id) {
        return UserMapper.mapToUserDto(
                userStorage.findUserById(id)
        );
    }

    // UserService.java
    public void addFriend(long userId, long friendId) {
        if (userId == friendId) {
            throw new ValidationException("Нельзя добавить самого себя в друзья.");
        }

        userStorage.findUserById(userId);
        userStorage.findUserById(friendId);

        // Добавляем взаимную связь
        userStorage.addFriend(userId, friendId);
        userStorage.addFriend(friendId, userId);
    }

    public void deleteFriend(long userId, long friendId) {
        if (userId == friendId) {
            log.warn("Попытка удаления самого себя из друзей: {} -> {}", userId, friendId);
            throw new ValidationException("Нельзя удалить себя из друзей");
        }

        // Проверка существования обоих пользователей
        userStorage.findUserById(userId);
        userStorage.findUserById(friendId);

        if (!userStorage.removeFriend(userId, friendId)) {
            log.warn("Дружба не найдена: {} -> {}", userId, friendId);
            throw new NotFoundException("Дружба между пользователями не существует");
        }
    }

    public Collection<UserDto> getFriendByUserId(long id) {
        return userStorage.getFriends(id)
                .stream()
                .map(UserMapper::mapToUserDto)
                .collect(Collectors.toList());
    }

    public Collection<UserDto> getCommonFriends(long userId, long friendId) {
        return userStorage.getCommonFriends(userId, friendId)
                .stream()
                .map(UserMapper::mapToUserDto)
                .collect(Collectors.toList());
    }

    public void deleteUserById(Long id) {
        // Убрана избыточная проверка
        userStorage.deleteById(id);
    }
}
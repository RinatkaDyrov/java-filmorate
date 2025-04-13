package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dal.film.FilmRepository;
import ru.yandex.practicum.filmorate.dal.like.LikeRepository;
import ru.yandex.practicum.filmorate.dto.film.FilmDto;
import ru.yandex.practicum.filmorate.dto.user.NewUserRequest;
import ru.yandex.practicum.filmorate.dto.user.UpdateUserRequest;
import ru.yandex.practicum.filmorate.dto.user.UserDto;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.mapper.FilmMapper;
import ru.yandex.practicum.filmorate.mapper.UserMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.interfaces.UserStorage;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class UserService {
    private final UserStorage userStorage;
    private final LikeRepository likeRepository;
    private final FilmRepository filmRepository;

    @Autowired
    public UserService(@Qualifier("userDbStorage") UserStorage userStorage, LikeRepository likeRepository, FilmRepository filmRepository) {
        this.userStorage = userStorage;
        this.likeRepository = likeRepository;
        this.filmRepository = filmRepository;
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
        return userStorage.findAll().stream().map(UserMapper::mapToUserDto).collect(Collectors.toList());
    }

    public UserDto getUserById(long id) {
        User user = userStorage.findUserById(id);
        return UserMapper.mapToUserDto(user);
    }

    public void addFriend(long userId, long friendId) {
        if (userId == friendId) {
            log.warn("Ошибка идентификационного номера при добавлении в друзья:" + " userId: {}, friendId: {}", userId, friendId);
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
            log.warn("Ошибка идентификационного номера при удалении из друзей:" + " userId: {}, friendId: {}", userId, friendId);
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
            log.info("Пользователи больше не дружат.(да и не дружили)");
        }
    }

    public Collection<UserDto> getFriendByUserId(long id) {
        log.info("Запрашиваются уже у сервиса друганы айдишки {}", id);
        return userStorage.getFriends(id).stream().map(UserMapper::mapToUserDto).collect(Collectors.toList());
    }

    public Collection<UserDto> getCommonFriends(long userId, long friendId) {
        log.debug("Получение списка общих друзей пользователей (ID: {}) и (ID: {})", userId, friendId);
        return userStorage.getCommonFriends(userId, friendId).stream().map(UserMapper::mapToUserDto).collect(Collectors.toList());
    }

    public List<FilmDto> getRecommendations(Long userId) {
        log.debug("Запрос на список рекомендаций для пользователя (Id: {}) в сервисе", userId);
        List<Long> recommendedFilmIds = likeRepository.findRecommendedFilmIds(userId);
        List<Film> films = filmRepository.findAllById(recommendedFilmIds);
        return films.stream().map(FilmMapper::mapToFilmDto).collect(Collectors.toList());
    }
}

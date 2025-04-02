package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dal.user.UserRepository;
import ru.yandex.practicum.filmorate.dto.user.NewUserRequest;
import ru.yandex.practicum.filmorate.dto.user.UpdateUserRequest;
import ru.yandex.practicum.filmorate.dto.user.UserDto;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.exception.DuplicatedDataException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.mapper.UserMapper;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class UserService {
    private final UserRepository userRepository;
    private final UserStorage userStorage;

    @Autowired
    public UserService(@Qualifier("userDbStorage") UserStorage userStorage, UserRepository userRepository1) {
        this.userStorage = userStorage;
        this.userRepository = userRepository1;
    }

    public UserDto createUser(NewUserRequest request) {
        if (request.getEmail() == null || request.getEmail().isEmpty()){
            throw new ConditionsNotMetException("Имейл должен быть указан");
        }
        userStorage.findUserByEmail(request.getEmail());

        User user = UserMapper.mapToUser(request);
        user = userRepository.save(user);
        return UserMapper.mapToUserDto(user);
    }

    public User updateUser(long id, UpdateUserRequest request) {
        User updUser = userStorage.findUserById(id);
        updUser = UserMapper.updateUserFields(updUser, request);
        updUser = userStorage.update(updUser);
        return updUser;
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

        if (user.getFriends().containsKey(friendId)) {
            log.warn("Попытка установить уже установленную дружбу");
            throw new ValidationException("Пользователи " + user.getName() + " и " + friend.getName() + " уже друзья");
        }

        user.getFriends().put(friendId, friend);
        friend.getFriends().put(userId, user);

        userStorage.update(user);
        userStorage.update(friend);
        log.info("Пользователи {} и {} теперь друзья", user.getName(), friend.getName());
    }

    public void deleteFriend(long userId, long friendId) {
        if (userId == friendId) {
            log.warn("Ошибка идентификационного номера при удалении из друзей:" +
                    " userId: {}, friendId: {}", userId, friendId);
            throw new ValidationException("Нельзя удалить самого себя из друзей.");
        }
        User user = userStorage.findUserById(userId);
        User friend = userStorage.findUserById(friendId);

        user.getFriends().remove(friendId);
        friend.getFriends().remove(userId);

        userStorage.update(user);
        userStorage.update(friend);
        log.info("Пользователи {} и {} больше не друзья", user.getName(), friend.getName());
    }

    public Collection<User> getFriendByUserId(long id) {
        log.debug("Получение списка друзей пользователя (ID: {})", id);
        return userStorage.findUserById(id).getFriends().values();
    }

    public Collection<User> getCommonFriends(long id, long otherId) {
        log.debug("Получение списка общих друзей пользователей (ID: {}) и (ID: {})", id, otherId);
        Collection<User> friendsByUser = userStorage.findUserById(id).getFriends().values();
        Collection<User> friendsByOtherUser = userStorage.findUserById(otherId).getFriends().values();
        return friendsByUser.stream()
                .filter(friendsByOtherUser::contains)
                .collect(Collectors.toList());
    }
}

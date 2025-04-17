package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dal.friendship.FriendshipRepository;
import ru.yandex.practicum.filmorate.dal.user.UserRepository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.interfaces.UserStorage;

import java.util.Collection;

@Slf4j
@Component("userDbStorage")
@RequiredArgsConstructor
public class UserDbStorage implements UserStorage {

    private final UserRepository userRepository;
    private final FriendshipRepository friendshipRepository;

    @Override
    public Collection<User> findAll() {
        log.debug("Запрос всех пользователей в хранилище");
        return userRepository.findAllUsers();
    }

    @Override
    public User findUserById(long id) {
        log.debug("Запрос пользователя по Id в хранилище");
        return userRepository.findUserById(id)
                .orElseThrow(() -> new NotFoundException("Пользователь с ID " + id + " не найден"));
    }

    @Override
    public User findUserByEmail(String email) {
        log.debug("Запрос пользователя по email в хранилище");
        return userRepository.findUserByEmail(email)
                .orElseThrow(() -> new NotFoundException("Пользователь с email " + email + " не найден"));
    }

    @Override
    public User create(User user) {
        log.debug("Создание пользователя в хранилище");
        validate(user);
        return userRepository.save(user);
    }

    private void validate(User user) {
        log.debug("Валидация пользователя (Id: {})", user.getId());
        if (user.getName().isEmpty()) {
            user.setName(user.getLogin());
        }
    }

    @Override
    public User update(User newUser) {
        log.debug("Обновление пользователя в хранилище");
        User oldUser = userRepository.findUserById(newUser.getId()).orElseThrow(() -> new NotFoundException("Нема такого"));
        System.out.println(oldUser.toString());
        log.debug("Обновление пользователя: email={}, login={}, name={}, birthday={}, id={}",
                newUser.getEmail(), newUser.getLogin(), newUser.getName(), newUser.getBirthday(), newUser.getId());
        return userRepository.update(newUser);
    }

    @Override
    public boolean addFriend(long userId, long friendId) {
        log.debug("Добавление пользователей в друзья в хранилище");
        boolean addingStatus = friendshipRepository.addFriendRequest(userId, friendId);
        if (addingStatus) {
            log.debug("Успешно");
        }
        return addingStatus;
    }

    @Override
    public void confirmFriendship(long userId, long friendId) {
        log.debug("Подтверждение дружбы в хранилище");
        friendshipRepository.confirmFriendship(userId, friendId);
    }

    @Override
    public Collection<User> getFriends(long userId) {
        log.debug("Запрос списка друзей пользователя в хранилище");
        return friendshipRepository.findFriendsByUserId(userId);
    }

    @Override
    public int getFriendsCount(long userId) {
        log.debug("Запрос кол-ва друзей пользователя в хранилище");
        return friendshipRepository.countFriendsByUserId(userId);
    }

    @Override
    public boolean removeFriend(long userId, long friendId) {
        log.debug("Запрос удаления из друзей от пользователя в хранилище");
        return friendshipRepository.removeFriend(userId, friendId);
    }

    @Override
    public Collection<User> getCommonFriends(long userId, long friendId) {
        log.debug("Запрос списка общих друзей пользователей в хранилище");
        return friendshipRepository.findCommonFriends(userId, friendId);
    }
    @Override
    public void deleteUser(Long id) {
        userRepository.deleteUser(id);
    }
}
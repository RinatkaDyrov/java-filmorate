package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dal.friendship.FriendshipRepository;
import ru.yandex.practicum.filmorate.dal.user.UserRepository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;

@Slf4j
@Component("userDbStorage")
public class UserDbStorage implements UserStorage {

    private final UserRepository userRepository;
    private final FriendshipRepository friendshipRepository;

    public UserDbStorage(UserRepository userRepository, FriendshipRepository friendshipRepository) {
        this.userRepository = userRepository;
        this.friendshipRepository = friendshipRepository;
    }

    @Override
    public Collection<User> findAll() {
        return userRepository.findAllUsers();
    }

    @Override
    public User findUserById(long id) {
        return userRepository.findUserById(id)
                .orElseThrow(() -> new NotFoundException("Пользователь с ID " + id + " не найден"));
    }

    @Override
    public User findUserByEmail(String email) {
        return userRepository.findUserByEmail(email)
                .orElseThrow(() -> new NotFoundException("Пользователь с email " + email + " не найден"));
    }

    @Override
    public User create(User user) {
        validate(user);
        return userRepository.save(user);
    }

    private void validate(User user) {
        if (user.getName().isEmpty()) {
            user.setName(user.getLogin());
        }

    }

    @Override
    public User update(User newUser) {
        User oldUser = userRepository.findUserById(newUser.getId()).orElseThrow(() -> new NotFoundException("Нема такого"));
        System.out.println(oldUser.toString());
        log.debug("Обновление пользователя: email={}, login={}, name={}, birthday={}, id={}",
                newUser.getEmail(), newUser.getLogin(), newUser.getName(), newUser.getBirthday(), newUser.getId());
        User updUSer = userRepository.update(newUser);
        System.out.println(updUSer.toString());
        return updUSer;
    }

    @Override
    public boolean addFriend(long userId, long friendId) {
        boolean addingStatus = friendshipRepository.addFriendRequest(userId, friendId);
        if (addingStatus) {
            log.debug("Успешно");
        }
        return addingStatus;
    }

    @Override
    public void confirmFriendship(long userId, long friendId) {
        friendshipRepository.confirmFriendship(userId, friendId);
    }

    @Override
    public Collection<User> getFriends(long userId) {
        return friendshipRepository.findFriendsByUserId(userId);
    }

    @Override
    public int getFriendsCount(long userId) {
        return friendshipRepository.countFriendsByUserId(userId);
    }

    @Override
    public boolean removeFriend(long userId, long friendId) {
        return friendshipRepository.removeFriend(userId, friendId);
    }

    @Override
    public Collection<User> getCommonFriends(long userId, long friendId) {
        return friendshipRepository.findCommonFriends(userId, friendId);
    }
}
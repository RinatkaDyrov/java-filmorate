package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dal.friendship.FriendshipRepository;
import ru.yandex.practicum.filmorate.dal.user.UserRepository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.ArrayList;
import java.util.Collection;

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
        return new ArrayList<>(userRepository.findAllUsers());
    }

    @Override
    public User findUserById(long id) {
        return userRepository.findUserById(id)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден с ID: " + id));
    }

    @Override
    public User findUserByEmail(String email) {
        return userRepository.findUserByEmail(email)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден с email: " + email));
    }

    @Override
    public User create(User user) {
        return userRepository.save(user);
    }

    @Override
    public User update(User newUser) {
        return userRepository.update(newUser);
    }

    public void addFriend(long userId, long friendId, boolean statusConfirm) {
        friendshipRepository.addFriendRequest(userId, friendId);
    }

    public void confirmFriendship(long userId, long friendId) {
        friendshipRepository.confirmFriendship(userId, friendId);
    }

    public Collection<User> getFriends(long userId) {
        // Получаем список друзей через FriendshipRepository
        return new ArrayList<>(friendshipRepository.findFriendsByUserId(userId));
    }

    public int getFriendCount(long userId) {
        return friendshipRepository.countFriendsByUserId(userId);
    }

    public boolean removeFriend(long userId, long friendId) {
        return friendshipRepository.removeFriend(userId, friendId);
    }

    public User updateWithFriendship(User user) {
        return userRepository.update(user);
    }
}
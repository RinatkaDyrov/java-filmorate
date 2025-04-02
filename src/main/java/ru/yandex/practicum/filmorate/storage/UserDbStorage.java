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
                .orElseThrow(() -> new NotFoundException("Пользователь с ID " + id + " не найден"));
    }

    @Override
    public User findUserByEmail(String email) {
        return userRepository.findUserByEmail(email)
                .orElseThrow(() -> new NotFoundException("Пользователь с email " + email + " не найден"));
    }

    @Override
    public User create(User user) {
        return userRepository.save(user);
    }

    @Override
    public User update(User newUser) {
        return userRepository.update(newUser);
    }

    @Override
    public boolean addFriend(long userId, long friendId) {
        return friendshipRepository.addFriendRequest(userId, friendId);
    }

    @Override
    public void confirmFriendship(long userId, long friendId) {
        friendshipRepository.confirmFriendship(userId, friendId);
    }

    @Override
    public Collection<User> getFriends(long userId) {
        return new ArrayList<>(friendshipRepository.findFriendsByUserId(userId));
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
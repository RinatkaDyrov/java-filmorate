package ru.yandex.practicum.filmorate.storage.interfaces;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;

public interface UserStorage {

    Collection<User> findAll();

    User findUserById(long id);

    User findUserByEmail(String email);

    User create(User user);

    User update(User newUser);

    default boolean addFriend(long userId, long friendId) {
        throw new UnsupportedOperationException("Не поддерживается в данном хранилище");
    }

    default void confirmFriendship(long userId, long friendId) {
        throw new UnsupportedOperationException("Не поддерживается в данном хранилище");
    }

    default Collection<User> getFriends(long userId) {
        throw new UnsupportedOperationException("Не поддерживается в данном хранилище");
    }

    default Collection<User> getCommonFriends(long userId, long friendId) {
        throw new UnsupportedOperationException("Не поддерживается в данном хранилище");
    }

    default int getFriendsCount(long userId) {
        throw new UnsupportedOperationException("Не поддерживается в данном хранилище");
    }

    default boolean removeFriend(long userId, long friendId) {
        throw new UnsupportedOperationException("Не поддерживается в данном хранилище");
    }
    void deleteById(Long id);
    boolean existsById(Long id);
}

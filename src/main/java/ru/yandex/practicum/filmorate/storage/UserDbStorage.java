package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
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
    private final JdbcTemplate jdbcTemplate;

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
        String sql = "DELETE FROM friendship WHERE user_id = ? AND friend_id = ?";
        int deleted = jdbcTemplate.update(sql, userId, friendId);
        return deleted > 0;
    }

    @Override
    public Collection<User> getCommonFriends(long userId, long friendId) {
        log.debug("Запрос списка общих друзей пользователей в хранилище");
        return friendshipRepository.findCommonFriends(userId, friendId);
    }

    @Override
    public void deleteById(Long id) {
        log.debug("Запрос на удаление пользователя по айди");
        try {
            // Проверка существования
            jdbcTemplate.queryForObject(
                    "SELECT id FROM users WHERE id = ?",
                    Long.class,
                    id
            );
        } catch (EmptyResultDataAccessException e) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Пользователь с ID " + id + " не найден"
            );
        }
        jdbcTemplate.update("DELETE FROM likes WHERE user_id = ?", id);
        jdbcTemplate.update("DELETE FROM friendship WHERE user_id = ? OR friend_id = ?", id, id);
        jdbcTemplate.update("DELETE FROM users WHERE id = ?", id);
    }

    @Override
    public boolean existsById(Long id) {
        log.debug("Запрос на проверку АйДи, как существующий");
        String sql = "SELECT COUNT(*) FROM users WHERE id = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, id);
        return count != null && count > 0;
    }
}

package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.*;

@Slf4j
@Component
public class InMemoryUserStorage implements UserStorage {

    private final Map<Long, User> users = new HashMap<>();
    private final Set<String> logins = new HashSet<>();
    private final Set<String> emails = new HashSet<>();

    @Override
    public Collection<User> findAll() {
        log.info("Выполняется запрос списка всех пользователей");
        return List.copyOf(users.values());
    }

    @Override
    public User findUserById(long id) {
        User user = users.get(id);
        if (user == null){
            throw new NotFoundException("Пользователь с ID " + id + " не найден");
        }
        return user;
    }

    @Override
    public User create(User user) {
        log.info("Добавление пользователя {}", user);
        validateUser(user, false);
        log.debug("Пользователь прошел валидацию на добавление");
        user.setId(getNextId());
        users.put(user.getId(), user);
        logins.add(user.getLogin());
        emails.add(user.getEmail());
        log.info("Пользователь {} добавлен", user.getLogin());
        return user;
    }

    @Override
    public User update(User newUser) {
        log.info("Обновление данных пользователя {}", newUser);
        validateUser(newUser, true);
        log.debug("Пользователь прошел валидацию на обновление");
        if (!users.get(newUser.getId()).getLogin().equalsIgnoreCase(newUser.getLogin())) {
            logins.remove(users.get(newUser.getId()).getLogin());
            logins.add(newUser.getLogin());
        }
        if (!users.get(newUser.getId()).getEmail().equalsIgnoreCase(newUser.getEmail())) {
            emails.remove(users.get(newUser.getId()).getEmail());
            emails.add(newUser.getEmail());
        }
        users.put(newUser.getId(), newUser);
        log.info("Пользователь с ID {} ({}) обновлен", newUser.getId(), newUser.getLogin());
        return newUser;
    }

    @Override
    public void addFriend(long userId, long friendId) {
        User user = findUserById(userId);
        User friend = findUserById(friendId);
        user.
    }

    private void validateUser(User user, boolean isUpdate) {
        log.debug("Запуск валидации пользователя");
        if (isUpdate) {
            if (user.getId() == null) {
                log.warn("Попытка обновления пользователя без ID");
                throw new NotFoundException("ID пользователя не может быть пустым при обновлении");
            }
            if (!users.containsKey(user.getId())) {
                log.warn("Пользователь с ID {} не найден", user.getId());
                throw new NotFoundException("Пользователь с таким ID не найден");
            }
        }
        if (emails.contains(user.getEmail())) {
            log.warn("Имейл {} уже занят", user.getEmail());
            throw new ValidationException("Пользователь с таким имейлом уже существует");
        }
        if (logins.contains(user.getLogin())) {
            log.warn("Логин {} уже занят", user.getLogin());
            throw new ValidationException("Пользователь с таким логином уже существует");
        }
        if (user.getBirthday().isAfter(LocalDate.now())) {
            log.warn("Дата рождения пользователя {} позже текущей", user.getBirthday());
            throw new ValidationException("Дата рождения не может быть в будущем");
        }
        if (user.getName() == null) {
            log.debug("Поле \"имя\" пользователя не задано." +
                    " Переназначение имени значением логина: {}", user.getLogin());
            user.setName(user.getLogin());
        }
    }

    private long getNextId() {
        log.debug("Генерация идентификационного номера пользователя");
        long currentMaxId = users.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}

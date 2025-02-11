package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/users")
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);
    private final Map<Long, User> users = new HashMap<>();

    @GetMapping
    public Collection<User> findAll() {
        logger.info("Выполняется запрос списка всех пользователей");
        return users.values();
    }

    @PostMapping
    public User create(@Valid @RequestBody User user) {
        logger.info("Добавление пользователя {}", user);
        validateUser(user, false);
        logger.debug("Пользователь прошел валидацию на добавление");
        user.setId(getNextId());
        users.put(user.getId(), user);
        logger.info("Пользователь {} добавлен", user.getLogin());
        return user;
    }

    @PutMapping
    public User update(@Valid @RequestBody User newUser) {
        logger.info("Обновление данных пользователя {}", newUser);
        validateUser(newUser, true);
        logger.debug("Пользователь прошел валидацию на обновление");
        users.put(newUser.getId(), newUser);
        logger.info("Пользователь с ID {} ({}) обновлен", newUser.getId(), newUser.getLogin());
        return newUser;
    }

    private void validateUser(User user, boolean isUpdate) {
        logger.debug("Запуск валидации пользователя");
        if (isUpdate) {
            if (user.getId() == null) {
                logger.warn("Попытка обновления пользователя без ID");
                throw new NotFoundException("ID пользователя не может быть пустым при обновлении");
            }
            if (!users.containsKey(user.getId())) {
                logger.warn("Пользователь с ID {} не найден", user.getId());
                throw new NotFoundException("Пользователь с таким ID не найден");
            }
        }

        users.values().stream()
                .filter(u -> !u.getId().equals(user.getId()))
                .forEach(u -> {
                    if (u.getEmail().equalsIgnoreCase(user.getEmail())) {
                        logger.warn("Имейл {} уже занят", user.getEmail());
                        throw new ValidationException("Пользователь с таким имейлом уже существует");
                    }
                    if (u.getLogin().equalsIgnoreCase(user.getLogin())) {
                        logger.warn("Логин {} уже занят", user.getLogin());
                        throw new ValidationException("Пользователь с таким логином уже существует");
                    }
                });

        if (user.getBirthday().isAfter(LocalDate.now())) {
            logger.warn("Дата рождения пользователя {} позже текущей", user.getBirthday());
            throw new ValidationException("Дата рождения не может быть в будущем");
        }
        if (user.getName() == null) {
            logger.debug("Поле \"имя\" пользователя не задано." +
                    " Переназначение имени значением логина: {}", user.getLogin());
            user.setName(user.getLogin());
        }
    }

    private long getNextId() {
        logger.debug("Генерация идентификационного номера");
        long currentMaxId = users.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}

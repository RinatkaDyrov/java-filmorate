package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
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
        logger.debug("Пользователь прошел валидацию");
        user.setId(getNextId());
        users.put(user.getId(), user);
        logger.info("Пользователь {} добавлен", user);
        return user;
    }

    @PutMapping
    public User update(@Valid @RequestBody User newUser) {
        logger.info("Обновление данных пользователя {}", newUser);
        validateUser(newUser, true);
        logger.debug("Пользователь прошел валидацию");
        users.put(newUser.getId(), newUser);
        logger.info("Пользователь {} обновлен", newUser);
        return newUser;
    }

    private void validateUser(User user, boolean isUpdate) {
        logger.debug("Запуск валидации пользователя");
        if (isUpdate && user.getId() == null) {
            logger.warn("ID пользователя не указан");
            throw new ValidationException("ID пользователя не может быть пустым при обновлении");
        }
        if (isUpdate && !users.containsKey(user.getId())){
            logger.warn("ID пользователя {} не найден в списке", user.getId());
            throw new ValidationException("Пользователь с таким ID не найден");
        }
        if (users.values()
                .stream()
                .anyMatch(u -> u.getEmail().equalsIgnoreCase(user.getEmail()))){
            logger.warn("Указанный имейл пользователя {} занят", user.getEmail());
            throw new ValidationException("Пользователь с таким имейл уже существует");
        }
        if (users.values()
                .stream()
                .anyMatch(u -> u.getLogin().equalsIgnoreCase(user.getLogin()))){
            logger.warn("Указанный логин пользователя {} занят", user.getLogin());
            throw new ValidationException("Пользователь с таким логином уже существует");
        }
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

package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dto.user.NewUserRequest;
import ru.yandex.practicum.filmorate.dto.user.UpdateUserRequest;
import ru.yandex.practicum.filmorate.dto.user.UserDto;
import ru.yandex.practicum.filmorate.service.UserService;

import java.util.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
@Slf4j
public class UserController {

    private final UserService userService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Collection<UserDto> findAll() {
        return userService.getAllUsers();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto create(@Valid @RequestBody NewUserRequest userRequest) {
        log.debug("Добавление нового пользователя");
        return userService.createUser(userRequest);
    }

    @PutMapping
    @ResponseStatus(HttpStatus.OK)
    public UserDto update(@Valid @RequestBody UpdateUserRequest updateUserRequest) {
        log.debug("Обновление пользователя (Id: {})", updateUserRequest.getId());
        return userService.updateUser(updateUserRequest.getId(), updateUserRequest);
    }

    @PutMapping("/{id}/friends/{friendId}")
    @ResponseStatus(HttpStatus.OK)
    public void addFriend(@PathVariable long id,
                          @PathVariable long friendId) {
        log.debug("Пользователь (Id: {}) добавляет в друзья пользователя (Id: {})", id, friendId);
        userService.addFriend(id, friendId);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteFriend(@PathVariable long id,
                             @PathVariable long friendId) {
        log.debug("Пользователь (Id: {}) удаляет пользователя (Id: {}) из друзей", id, friendId);
        userService.deleteFriend(id, friendId);
    }

    @GetMapping("/{id}/friends")
    @ResponseStatus(HttpStatus.OK)
    public Collection<UserDto> getFriendsByUserId(@PathVariable long id) {
        log.debug("Запрос на список друзей пользователя (Id: {})", id);
        userService.getUserById(id);
        List<UserDto> friends = (List<UserDto>) userService.getFriendByUserId(id);
        if (friends == null || friends.isEmpty()) {
            log.warn("Пользователь не найден или не имеет друзей");
            return Collections.emptyList();
        }
        return friends;
    }

    @GetMapping("/{id}/friends/common/{friendId}")
    @ResponseStatus(HttpStatus.OK)
    public Collection<UserDto> getCommonFriends(@PathVariable long id,
                                                @PathVariable long friendId) {
        log.debug("Запрос на список общих друзей пользователя (Id: {}) и пользователя (Id: {})", id, friendId);
        return userService.getCommonFriends(id, friendId);
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUserById(id);
        return ResponseEntity.noContent().build();
    }

}

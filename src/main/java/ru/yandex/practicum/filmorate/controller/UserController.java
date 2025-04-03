package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
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
        return userService.createUser(userRequest);
    }

    @PutMapping
    @ResponseStatus(HttpStatus.OK)
    public UserDto update(@Valid @RequestBody UpdateUserRequest updateUserRequest) {

        log.info("Получен запрос на обновление пользователя ID {}: {}", updateUserRequest.getId(), updateUserRequest);
        return userService.updateUser(updateUserRequest.getId(), updateUserRequest);
    }

    @PutMapping("/{id}/friends/{friendId}")
    @ResponseStatus(HttpStatus.OK)
    public void addFriend(@PathVariable long id,
                          @PathVariable long friendId) {
        userService.addFriend(id, friendId);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteFriend(@PathVariable long id,
                             @PathVariable long friendId) {
        log.info("Удаляем пользователей {}, {} из друзей", id, friendId);
        userService.deleteFriend(id, friendId);
    }

    @GetMapping("/{id}/friends")
    @ResponseStatus(HttpStatus.OK)
    public Collection<UserDto> getFriendsByUserId(@PathVariable long id) {
        log.info("Запрашиваются друганы айдишки {}",id);
        userService.getUserById(id);
        List<UserDto> friends = (List<UserDto>) userService.getFriendByUserId(id);
        if (friends == null || friends.isEmpty()) {
            return Collections.emptyList();  // Возвращаем пустой список, если нет друзей
        }
        return friends;
    }

    @GetMapping("/{id}/friends/common/{friendId}")
    @ResponseStatus(HttpStatus.OK)
    public Collection<UserDto> getListOfCommonFriends(@PathVariable long id,
                                                   @PathVariable long friendId) {
        return userService.getCommonFriends(id, friendId);
    }
}

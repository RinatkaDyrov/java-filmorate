package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.*;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserStorage userStorage, UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public Collection<User> findAll() {
        return userService.getAllUsers();
    }

    @PostMapping
    public User create(@Valid @RequestBody User user) {
        return userService.createUser(user);
    }

    @PutMapping
    public User update(@Valid @RequestBody User newUser) {
        return userService.updateUser(newUser);
    }

    @PutMapping("/{id}/friends/{friendId}")
    public void addFriend(@PathVariable("id") long id,
                          @PathVariable("friendId") long friendId) {
        userService.addFriend(id, friendId);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public void deleteFriend(@PathVariable("id") long id,
                             @PathVariable("friendId") long friendId) {
        userService.deleteFriend(id, friendId);
    }

    @GetMapping("/{id}/friends")
    public Collection<User> getFriendsByUserId(@PathVariable("id") long id) {
        return userService.getFriendByUserId(id);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public Collection<User> getListOfCommonFriends(@PathVariable("id") long id,
                                                   @PathVariable("otherId") long otherId) {
        return userService.getCommonFriends(id, otherId);
    }
}

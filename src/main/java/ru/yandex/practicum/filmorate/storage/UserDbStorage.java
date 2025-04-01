package ru.yandex.practicum.filmorate.storage;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dal.user.UserRepository;
import ru.yandex.practicum.filmorate.dto.user.UserDto;
import ru.yandex.practicum.filmorate.mapper.UserMapper;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Component("dbStorage")
public class UserDbStorage implements UserStorage {

    private final UserRepository userRepository;

    public UserDbStorage(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public Collection<UserDto> findAll() {
        return userRepository.findAllUsers()
                .stream().map(UserMapper::mapToUserDto)
                .collect(Collectors.toList());
    }

    @Override
    public User findUserById(long id) {
        return null;
    }

    @Override
    public User create(User user) {
        return null;
    }

    @Override
    public User update(User newUser) {
        return null;
    }

    @Override
    public User updateWithFriendship(User user) {
        return null;
    }
}

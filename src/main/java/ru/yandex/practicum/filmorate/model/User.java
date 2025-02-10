package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Past;
import lombok.Data;

import java.time.LocalDate;

@Data
public class User {
    Long id;

    @Email(message = "Электронная почта не может быть пустой и должна содержать символ @")
    String email;

    String login;

    String name;

    LocalDate birthday;
}

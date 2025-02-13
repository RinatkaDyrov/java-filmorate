package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.time.LocalDate;

@Data
public class User {
    private Long id;

    @Email(message = "Электронная почта не может быть пустой и должна содержать символ @")
    @NotBlank
    private String email;

    @NotBlank
    @Pattern(regexp = "^[^\\s]+$", message = "Логин не должен содержать пробелы")
    private String login;

    private String name;

    private LocalDate birthday;
}

package ru.yandex.practicum.filmorate.dto.director;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class NewDirectorRequest {
    private long id;
    @NotNull(message = "Имя режиссера не может быть пустым.")
    private String name;
}

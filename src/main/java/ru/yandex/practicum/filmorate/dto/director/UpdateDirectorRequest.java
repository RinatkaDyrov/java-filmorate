package ru.yandex.practicum.filmorate.dto.director;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateDirectorRequest {
    private long id;
    @NotNull(message = "Имя режиссера не может быть пустым.")
    private String name;

    public boolean hasName() {
        return !(name == null || name.isBlank());
    }
}

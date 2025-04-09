package ru.yandex.practicum.filmorate.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.dto.director.DirectorDto;
import ru.yandex.practicum.filmorate.model.Director;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DirectorMapper {

    public static DirectorDto mapToDirectorDto(Director director) {
        log.debug("Конвертируем Director в DirectorDto");
        DirectorDto dto = new DirectorDto();
        dto.setId(director.getId());
        dto.setName(director.getName());
        return dto;
    }
}

package ru.yandex.practicum.filmorate.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.dto.rating.MpaDto;
import ru.yandex.practicum.filmorate.model.Mpa;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Slf4j
public class MpaMapper {
    public static MpaDto mapToMpaDto(Mpa mpa) {
        log.debug("Конвертируем Mpa в MpaDto");
        MpaDto dto = new MpaDto();
        dto.setId(mpa.getId());
        dto.setName(mpa.getName());
        return dto;
    }
}

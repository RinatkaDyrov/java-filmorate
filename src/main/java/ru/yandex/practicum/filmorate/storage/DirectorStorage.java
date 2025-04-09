package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dal.director.DirectorRepository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Director;

import java.util.Collection;

@Slf4j
@Component
@RequiredArgsConstructor
public class DirectorStorage {
    private final DirectorRepository directorRepository;


    public Collection<Director> findAll() {
        log.debug("Запрос всех режиссеров в хранилище");
        return directorRepository.findAllDirectors();
    }

    public Director findById(long id) {
        log.debug("Поиск режиссера (ID: {}) в хранилище", id);
        return directorRepository.findDirectorById(id)
                .orElseThrow(() -> new NotFoundException("Режиссер с ID: " + id + " не найден"))    ;
    }

    public Director create(Director director) {
        log.debug("Создание режиссера в хранилище");
        return directorRepository.save(director);
    }

    public Director update(Director updDirector) {
        log.debug("Обновление режиссера в хранилище");
        return directorRepository.updateDirector(updDirector);
    }

    public void delete(long id) {
        log.debug("Удаление режиссера (ID: {}) в хранилище", id);
        directorRepository.delete(id);
    }
}

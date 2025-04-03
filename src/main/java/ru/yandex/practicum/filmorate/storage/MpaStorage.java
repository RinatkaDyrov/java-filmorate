package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dal.rating.MpaRepository;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.Collection;

@Slf4j
@Component
public class MpaStorage {

    private final MpaRepository mpaRepository;

    public MpaStorage(MpaRepository mpaRepository) {
        this.mpaRepository = mpaRepository;
    }

    public Collection<Mpa> findAll() {
        return mpaRepository.findAll();
    }

    public Mpa findById(long id) {
        log.info("Ищем рейтинг в хранилище по id {}", id);
        return mpaRepository.findById(id);
    }
}

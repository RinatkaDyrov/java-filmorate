package ru.yandex.practicum.filmorate.storage.interfaces;

import ru.yandex.practicum.filmorate.model.Review;

import java.util.List;
import java.util.Optional;

public interface ReviewStorage {
    Review create(Review review);

    Review update(Review review);

    void deleteById(Long reviewId);

    Optional<Review> findById(Long reviewId);

    List<Review> findAllByFilmId(Long filmId, int count);

    List<Review> findAll();

    void addLike(Long reviewId, Long userId);

    void addDislike(Long reviewId, Long userId);

    void removeReaction(Long reviewId, Long userId);
}
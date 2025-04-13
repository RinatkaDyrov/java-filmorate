package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dal.review.ReviewRepository;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.interfaces.ReviewStorage;

import java.util.List;
import java.util.Optional;

@Slf4j
@Component("ReviewDbStorage")
@RequiredArgsConstructor
public class ReviewDbStorage implements ReviewStorage {
    private final ReviewRepository reviewRepository;

    @Override
    public Review create(Review review) {
        log.info("Создание отзыва: {}", review);
        return reviewRepository.create(review);
    }

    @Override
    public Review update(Review review) {
        log.info("Обновление отзыва: {}", review);
        return reviewRepository.update(review);
    }

    @Override
    public void deleteById(Long reviewId) {
        log.info("Удаление отзыва с ID: {}", reviewId);
        reviewRepository.deleteById(reviewId);
    }

    @Override
    public Optional<Review> findById(Long reviewId) {
        log.info("Поиск отзыва с ID: {}", reviewId);
        return reviewRepository.findById(reviewId);
    }

    @Override
    public List<Review> findAllByFilmId(Long filmId, int count) {
        log.info("Поиск отзывов для фильма с ID: {} с количеством: {}", filmId, count);
        return reviewRepository.findAllByFilmId(filmId, count);
    }

    @Override
    public List<Review> findAll(int count) {
        log.info("Поиск всех отзывов с количеством: {}", count);
        return reviewRepository.findAll(count);
    }

    @Override
    public void addLike(Long reviewId, Long userId) {
        log.info("Добавление лайка к отзыву с ID: {} от пользователя с ID: {}", reviewId, userId);
        reviewRepository.addLike(reviewId, userId);
    }

    @Override
    public void addDislike(Long reviewId, Long userId) {
        log.info("Добавление дизлайка к отзыву с ID: {} от пользователя с ID: {}", reviewId, userId);
        reviewRepository.addDislike(reviewId, userId);
    }

    @Override
    public void removeReaction(Long reviewId, Long userId) {
        log.info("Удаление реакции от отзыва с ID: {} от пользователя с ID: {}", reviewId, userId);
        reviewRepository.removeReaction(reviewId, userId);
    }
}

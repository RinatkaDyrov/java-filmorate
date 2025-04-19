package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dal.event.EventRepository;
import ru.yandex.practicum.filmorate.dal.review.ReviewRepository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.interfaces.ReviewStorage;

import java.util.List;
import java.util.Optional;

@Slf4j
@Component("ReviewDbStorage")
@RequiredArgsConstructor
public class ReviewDbStorage implements ReviewStorage {
    private final ReviewRepository reviewRepository;
    private final EventRepository eventRepository;

    @Override
    public Review create(Review review) {
        log.info("Создание отзыва: {}", review);
        Review newReview = reviewRepository.create(review);
        eventRepository.addReviewEvent(newReview.getUserId(), newReview.getReviewId());
        return newReview;
    }

    @Override
    public Review update(Review review) {
        log.info("Обновление отзыва: {}", review);
        Review updReview = reviewRepository.update(review);
        eventRepository.updateReviewEvent(updReview.getUserId(), updReview.getReviewId());
        return updReview;
    }

    @Override
    public void deleteById(Long reviewId) {
        log.info("Удаление отзыва с ID: {}", reviewId);
        Review deletedReview = findById(reviewId)
                .orElseThrow(() -> new NotFoundException("Ревью с ID: " + reviewId + " не найдено."));
        eventRepository.removeReviewEvent(deletedReview.getUserId(), reviewId);
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
    public List<Review> findAll() {
        log.info("Поиск всех отзывов");
        return reviewRepository.findAll();
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
package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dto.review.NewReviewRequest;
import ru.yandex.practicum.filmorate.dto.review.ReviewDto;
import ru.yandex.practicum.filmorate.dto.review.UpdateReviewRequest;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.service.ReviewService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/reviews")
@RequiredArgsConstructor
public class ReviewController {
    private final ReviewService reviewService;

    @PostMapping
    ReviewDto create(@Valid @RequestBody NewReviewRequest request) {
        log.debug("Запрос на создание отзыва");
        return reviewService.createReview(request);
    }

    @GetMapping("{id}")
    ReviewDto findById(@PathVariable Long id) {
        log.debug("Запрос на поиск отзыва по ID: {}", id);
        return reviewService.getReviewById(id);
    }

    @PutMapping
    ReviewDto update(@RequestBody UpdateReviewRequest request) {
        log.debug("Запрос на обновление отзыва ID: {}", request.getReviewId());
        return reviewService.updateReview(request);
    }

    @DeleteMapping("{id}")
    void delete(@PathVariable Long id) {
        log.debug("Запрос на удаление отзыва по ID: {}", id);
        reviewService.deleteReview(id);
    }

    @GetMapping
    public List<Review> findAll(
            @RequestParam(required = false) Long filmId,
            @RequestParam(defaultValue = "10") int count
    ) {
        log.debug("Запрос на поиск всех отзывов");
        return reviewService.getReviews(filmId, count);
    }

    @PutMapping("/{id}/like/{userId}")
    public void like(@PathVariable Long id, @PathVariable Long userId) {
        log.debug("Пользователь ID: {} ставит лайк на отзыв ID: {}", userId, id);
        reviewService.addLike(id, userId);
    }

    @PutMapping("/{id}/dislike/{userId}")
    public void dislike(@PathVariable Long id, @PathVariable Long userId) {
        log.debug("Пользователь ID: {} ставит дизлайк на отзыв ID: {}", userId, id);
        reviewService.addDislike(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void deleteLike(@PathVariable Long id, @PathVariable Long userId) {
        log.debug("Пользователь ID: {} удаляет лайк с отзыва ID: {}", userId, id);
        reviewService.removeReaction(id, userId);
    }

    @DeleteMapping("/{id}/dislike/{userId}")
    public void deleteDislike(@PathVariable Long id, @PathVariable Long userId) {
        log.debug("Пользователь ID: {} удаляет дизлайк с отзыва ID: {}", userId, id);
        reviewService.removeReaction(id, userId);
    }
}
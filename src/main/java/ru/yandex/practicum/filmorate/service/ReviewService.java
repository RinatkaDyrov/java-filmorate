package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.review.NewReviewRequest;
import ru.yandex.practicum.filmorate.dto.review.ReviewDto;
import ru.yandex.practicum.filmorate.dto.review.UpdateReviewRequest;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.mapper.ReviewMapper;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.ReviewDbStorage;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReviewService {
    private final ReviewDbStorage reviewDbStorage;

    public ReviewDto createReview(NewReviewRequest request) {
        log.debug("Создание отзыва в сервисе");
        if (request.getUserId() == null || request.getUserId() < 0) {
            throw new NotFoundException("ID пользователя должен быть больше 0");
        }
        if (request.getFilmId() == null || request.getFilmId() < 0) {
            throw new NotFoundException("ID пользователя должен быть больше 0");
        }
        Review review = ReviewMapper.mapToReview(request);
        review = reviewDbStorage.create(review);

        return ReviewMapper.mapToReviewDto(review);
    }

    public ReviewDto updateReview(UpdateReviewRequest request) {
        log.debug("Обновление отзыва в сервисе");
        Review updReview = reviewDbStorage.findById(request.getReviewId())
                .orElseThrow(() -> new NotFoundException("Ревью с ID: " + request.getReviewId() + " не найдено"));
        ReviewMapper.updateReviewFields(updReview, request);
        updReview = reviewDbStorage.update(updReview);
        return ReviewMapper.mapToReviewDto(updReview);
    }

    public void deleteReview(Long id) {
        log.debug("Удаление отзыва в сервисе");
        reviewDbStorage.deleteById(id);
    }

    public ReviewDto getReviewById(Long id) {
        log.debug("Получение отзыва по ID в сервисе");
        Review review = reviewDbStorage.findById(id)
                .orElseThrow(() -> new NotFoundException("Отзыв не найден: " + id));
        return ReviewMapper.mapToReviewDto(review);
    }

    public List<Review> getReviews(Long filmId, int count) {
        log.debug("Получение списка отзывов в сервисе");
        return (filmId != null)
                ? reviewDbStorage.findAllByFilmId(filmId, count)
                : reviewDbStorage.findAll();
    }

    public void addLike(Long reviewId, Long userId) {
        log.debug("Лайк для отзыва в сервисе");
        reviewDbStorage.addLike(reviewId, userId);
    }

    public void addDislike(Long reviewId, Long userId) {
        log.debug("Дизлайк для отзыва в сервисе");
        reviewDbStorage.addDislike(reviewId, userId);
    }

    public void removeReaction(Long reviewId, Long userId) {
        log.debug("Удаление лайка/дизлайка для отзыва в сервисе");
        reviewDbStorage.removeReaction(reviewId, userId);
    }
}

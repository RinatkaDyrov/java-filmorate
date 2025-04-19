package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.ReviewDbStorage;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewService {
    private final ReviewDbStorage reviewDbStorage;

    public Review createReview(Review review) {
        if (review.getUserId() == null || review.getUserId() < 0) {
            throw new NotFoundException("ID пользователя должен быть больше 0");
        }
        if (review.getFilmId() == null || review.getFilmId() < 0) {
            throw new NotFoundException("ID пользователя должен быть больше 0");
        }
        return reviewDbStorage.create(review);
    }

    public Review updateReview(Review review) {
        return reviewDbStorage.update(review);
    }

    public void deleteReview(Long id) {
        reviewDbStorage.deleteById(id);
    }

    public Review getReviewById(Long id) {
        return reviewDbStorage.findById(id)
                .orElseThrow(() -> new NotFoundException("Отзыв не найден: " + id));
    }

    public List<Review> getReviews(Long filmId, int count) {
        return (filmId != null)
                ? reviewDbStorage.findAllByFilmId(filmId, count)
                : reviewDbStorage.findAll();
    }

    public void addLike(Long reviewId, Long userId) {
        reviewDbStorage.addLike(reviewId, userId);
    }

    public void addDislike(Long reviewId, Long userId) {
        reviewDbStorage.addDislike(reviewId, userId);
    }

    public void removeReaction(Long reviewId, Long userId) {
        reviewDbStorage.removeReaction(reviewId, userId);
    }
}

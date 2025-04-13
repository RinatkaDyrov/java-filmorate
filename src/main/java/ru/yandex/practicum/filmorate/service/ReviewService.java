package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dal.review.ReviewRepository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Review;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewService {
    private final ReviewRepository reviewRepository;

    public Review createReview(Review review) {
        if (review.getUserId() == null || review.getUserId() < 0) {
            throw new NotFoundException("ID пользователя должен быть больше 0");
        }
        if (review.getFilmId() == null || review.getFilmId() < 0) {
            throw new NotFoundException("ID пользователя должен быть больше 0");
        }
        return reviewRepository.create(review);
    }

    public Review updateReview(Review review) {
        return reviewRepository.update(review);
    }

    public void deleteReview(Long id) {
        reviewRepository.deleteById(id);
    }

    public Review getReviewById(Long id) {
        return reviewRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Отзыв не найден: " + id));
    }

    public List<Review> getReviews(Long filmId, int count) {
        return (filmId != null)
                ? reviewRepository.findAllByFilmId(filmId, count)
                : reviewRepository.findAll(count);
    }

    public void addLike(Long reviewId, Long userId) {
        reviewRepository.addLike(reviewId, userId);
    }

    public void addDislike(Long reviewId, Long userId) {
        reviewRepository.addDislike(reviewId, userId);
    }

    public void removeReaction(Long reviewId, Long userId) {
        reviewRepository.removeReaction(reviewId, userId);
    }
}

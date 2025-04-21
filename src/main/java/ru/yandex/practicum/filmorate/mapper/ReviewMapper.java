package ru.yandex.practicum.filmorate.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.dto.review.NewReviewRequest;
import ru.yandex.practicum.filmorate.dto.review.ReviewDto;
import ru.yandex.practicum.filmorate.dto.review.UpdateReviewRequest;
import ru.yandex.practicum.filmorate.model.Review;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Slf4j
public class ReviewMapper {

    public static ReviewDto mapToReviewDto(Review review) {
        log.debug("Конвертируем Review в ReviewDto");
        ReviewDto dto = new ReviewDto();
        dto.setReviewId(review.getReviewId());
        dto.setContent(review.getContent());
        dto.setIsPositive(review.getIsPositive());
        dto.setUserId(review.getUserId());
        dto.setFilmId(review.getFilmId());
        dto.setUseful(review.getUseful());
        return dto;
    }

    public static Review mapToReview(NewReviewRequest request) {
        log.debug("Конвертируем запрос в Review");
        Review review = new Review();
        review.setContent(request.getContent());
        review.setIsPositive(request.getIsPositive());
        review.setUserId(request.getUserId());
        review.setFilmId(request.getFilmId());
        review.setUseful(request.getUseful());
        return review;
    }

    public static Review updateReviewFields(Review review, UpdateReviewRequest request) {
        log.debug("Обновление параметров ревью (Id: {})", review.getReviewId());
        if (request.hasContent()) {
            review.setContent(request.getContent());
        }
        if (request.hasIsPositive()) {
            review.setIsPositive(request.getIsPositive());
        }
        if (request.hasUserId()) {
            review.setUserId(request.getUserId());
        }
        if (request.hasFilmId()) {
            review.setFilmId(request.getFilmId());
        }
        if (request.hasUseful()) {
            review.setUseful(review.getUseful());
        }
        return review;
    }
}

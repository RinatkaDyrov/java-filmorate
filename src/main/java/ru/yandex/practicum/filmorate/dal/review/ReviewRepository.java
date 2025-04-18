package ru.yandex.practicum.filmorate.dal.review;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dal.BaseRepository;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.interfaces.ReviewStorage;

import java.util.List;
import java.util.Optional;

@Slf4j
@Repository
public class ReviewRepository extends BaseRepository<Review> implements ReviewStorage {
    private static final String FIND_ALL_REVIEW = "SELECT * FROM review ORDER BY useful DESC";

    private static final String CREATE_REVIEW = """
            INSERT INTO review (film_id,
                                user_id,
                                content,
                                is_positive,
                                useful
            )
            VALUES (?, ?, ?, ?, ?)
            """;

    private static final String UPDATE_REVIEW = """
            UPDATE review
            SET content = ?,
                is_positive = ?
            WHERE review_id = ?
            """;

    private static final String FIND_BY_ID_REVIEW = "SELECT * FROM review WHERE review_id = ?";
    private static final String DELETE_BY_ID_REVIEW = "DELETE FROM review WHERE review_id = ?";
    private static final String ADD_LIKE_REVIEW = """
            INSERT INTO review_users (review_id,
                                      user_id,
                                      is_useful
            )
            VALUES (?, ?, TRUE)
            """;
    private static final String ADD_DIS_LIKE_REVIEW = """
            INSERT INTO review_users (review_id,
                                      user_id,
                                      is_useful
            )
            VALUES (?, ?, FALSE)
            """;
    private static final String GET_RATING_TYPE_REVIEW = """
            SELECT is_useful
            FROM review_users
            WHERE review_id = ? AND user_id = ?
            """;

    public ReviewRepository(JdbcTemplate jdbc, RowMapper<Review> mapper) {
        super(jdbc, mapper);
    }

    @Override
    public List<Review> findAll() {
        return jdbc.query(FIND_ALL_REVIEW, mapper);
    }

    @Override
    public Review create(Review review) {
        long id = super.insert(CREATE_REVIEW,
                review.getFilmId(),
                review.getUserId(),
                review.getContent(),
                review.getIsPositive(),
                review.getUseful()
        );
        review.setReviewId(id);
        return review;
    }

    @Override
    public Review update(Review review) {
        Optional<Review> existingReview = findById(review.getReviewId());
        if (existingReview.isPresent()) {
            super.update(UPDATE_REVIEW,
                    review.getContent(),
                    review.getIsPositive(),
                    review.getReviewId());
            return findById(review.getReviewId())
                    .orElseThrow(() -> new RuntimeException("Не удалось получить обновленный отзыв с ID: " + review.getReviewId()));
        } else {
            throw new RuntimeException("Отзыв с ID " + review.getReviewId() + " не существует.");
        }
    }

    @Override
    public void deleteById(Long reviewId) {
        jdbc.update(DELETE_BY_ID_REVIEW, reviewId);
    }

    @Override
    public Optional<Review> findById(Long reviewId) {
        return jdbc.query(
                        FIND_BY_ID_REVIEW, mapper, reviewId)
                .stream()
                .findFirst();
    }

    @Override
    public List<Review> findAllByFilmId(Long filmId, int count) {
        String sql = filmId != null
                ? "SELECT * FROM review WHERE film_id = ? ORDER BY useful DESC LIMIT ?"
                : "SELECT * FROM review ORDER BY useful DESC LIMIT ?";
        return filmId != null
                ? jdbc.query(sql, mapper, filmId, count)
                : jdbc.query(sql, mapper, count);
    }

    @Override
    public void addLike(Long reviewId, Long userId) {
        removeReaction(reviewId, userId); // удаляем, если был дизлайк
        jdbc.update(ADD_LIKE_REVIEW, reviewId, userId);
        incrementUseful(reviewId);
    }

    @Override
    public void addDislike(Long reviewId, Long userId) {
        removeReaction(reviewId, userId); // удаляем, если был лайк
        jdbc.update(ADD_DIS_LIKE_REVIEW, reviewId, userId);
        decrementUseful(reviewId);
    }

    @Override
    public void removeReaction(Long reviewId, Long userId) {
        Boolean wasUseful = getRatingType(reviewId, userId);
        jdbc.update("DELETE FROM review_users WHERE review_id = ? AND user_id = ?", reviewId, userId);
        if (wasUseful != null) {
            if (wasUseful) decrementUseful(reviewId);
            else incrementUseful(reviewId);
        }
    }

    private Boolean getRatingType(Long reviewId, Long userId) {
        List<Boolean> result = jdbc.query(GET_RATING_TYPE_REVIEW,
                (rs, rowNum) -> rs.getBoolean("is_useful"), reviewId, userId);
        return result.isEmpty() ? null : result.get(0);
    }

    private void incrementUseful(Long reviewId) {
        jdbc.update("UPDATE review SET useful = useful + 1 WHERE review_id = ?", reviewId);
    }

    private void decrementUseful(Long reviewId) {
        jdbc.update("UPDATE review SET useful = useful - 1 WHERE review_id = ?", reviewId);
    }
}

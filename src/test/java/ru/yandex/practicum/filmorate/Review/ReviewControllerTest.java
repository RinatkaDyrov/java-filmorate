package ru.yandex.practicum.filmorate.Review;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.yandex.practicum.filmorate.controller.ReviewController;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.service.ReviewService;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class ReviewControllerTest {

    @Mock
    private ReviewService reviewService;

    @InjectMocks
    private ReviewController reviewController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(reviewController).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    void createReviewTest() throws Exception {
        Review review = new Review(1L, "Тест Great movie!", true, 1L, 2L, 0);
        when(reviewService.createReview(any(Review.class))).thenReturn(review);

        mockMvc.perform(post("/reviews")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(review)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").value("Тест Great movie!"));

        ArgumentCaptor<Review> captor = ArgumentCaptor.forClass(Review.class);
        verify(reviewService).createReview(captor.capture());
        assertEquals("Тест Great movie!", captor.getValue().getContent());
    }

    @Test
    void findByIdTest() throws Exception {
        Review review = new Review(1L, "Тест Great movie!", true, 1L, 2L, 0);
        when(reviewService.getReviewById(anyLong())).thenReturn(review);

        mockMvc.perform(get("/reviews/{id}", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").value("Тест Great movie!"));

        verify(reviewService).getReviewById(1L);
    }

    @Test
    void updateReviewTest() throws Exception {
        Review review = new Review(1L, "Updated review", true, 1L, 2L, 0);
        when(reviewService.updateReview(any(Review.class))).thenReturn(review);

        mockMvc.perform(put("/reviews")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(review)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").value("Updated review"));

        ArgumentCaptor<Review> captor = ArgumentCaptor.forClass(Review.class);
        verify(reviewService).updateReview(captor.capture());
        assertEquals("Updated review", captor.getValue().getContent());
    }

    @Test
    void deleteReviewTest() throws Exception {
        doNothing().when(reviewService).deleteReview(anyLong());

        mockMvc.perform(delete("/reviews/{id}", 1))
                .andExpect(status().isOk());

        verify(reviewService).deleteReview(1L);
    }

    @Test
    void findAllReviewsTest() throws Exception {
        List<Review> reviews = Collections.singletonList(new Review(1L, "Тест Great movie!", true, 1L, 2L, 0));
        when(reviewService.getReviews(any(Long.class), anyInt())).thenReturn(reviews);

        mockMvc.perform(get("/reviews")
                        .param("filmId", "1")
                        .param("count", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].content").value("Тест Great movie!"));

        verify(reviewService).getReviews(1L, 10);
    }

    @Test
    void addLikeTest() throws Exception {
        doNothing().when(reviewService).addLike(anyLong(), anyLong());

        mockMvc.perform(put("/reviews/{id}/like/{userId}", 1, 2))
                .andExpect(status().isOk());

        verify(reviewService).addLike(1L, 2L);
    }

    @Test
    void addDislikeTest() throws Exception {
        doNothing().when(reviewService).addDislike(anyLong(), anyLong());

        mockMvc.perform(put("/reviews/{id}/dislike/{userId}", 1, 2))
                .andExpect(status().isOk());

        verify(reviewService).addDislike(1L, 2L);
    }

    @Test
    void deleteLikeTest() throws Exception {
        doNothing().when(reviewService).removeReaction(anyLong(), anyLong());

        mockMvc.perform(delete("/reviews/{id}/like/{userId}", 1, 2))
                .andExpect(status().isOk());

        verify(reviewService).removeReaction(1L, 2L);
    }

    @Test
    void deleteDislikeTest() throws Exception {
        doNothing().when(reviewService).removeReaction(anyLong(), anyLong());

        mockMvc.perform(delete("/reviews/{id}/dislike/{userId}", 1, 2))
                .andExpect(status().isOk());

        verify(reviewService).removeReaction(1L, 2L);
    }
}
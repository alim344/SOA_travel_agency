package com.example.tour_service.service;

import com.example.tour_service.DTO.ReviewDTO;
import com.example.tour_service.model.Review;
import com.example.tour_service.model.Tour;
import com.example.tour_service.model.TourStatus;
import com.example.tour_service.repo.ReviewRepository;
import com.example.tour_service.repo.TourRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReviewService {

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private TourRepository tourRepository;

    public ReviewDTO addReview(Long tourId, ReviewDTO request, Long currentUserId) {
        Tour tour = tourRepository.findById(tourId)
                .orElseThrow(() -> new RuntimeException("Tour not found with id: " + tourId));

        if (tour.getStatus() != TourStatus.PUBLISHED) {
            throw new RuntimeException("Reviews can only be added to PUBLISHED tours");
        }

        Review review = new Review();
        review.setRating(request.getRating());
        review.setComment(request.getComment());
        review.setTouristId(currentUserId);
        review.setTouristName(request.getTouristName());
        review.setTouristSurname(request.getTouristSurname());
        review.setVisitDate(request.getVisitDate());
        review.setCommentDate(LocalDateTime.now());
        review.setImages(request.getImages() != null ? request.getImages() : new ArrayList<>());
        review.setTour(tour);

        Review savedReview = reviewRepository.save(review);

        return mapToReviewResponseDTO(savedReview);
    }

    public List<ReviewDTO> getReviewsByTour(Long tourId) {
        List<Review> reviews = reviewRepository.findByTourId(tourId);
        return reviews.stream()
                .map(this::mapToReviewResponseDTO)
                .collect(Collectors.toList());
    }

    private ReviewDTO mapToReviewResponseDTO(Review review) {
        ReviewDTO dto = new ReviewDTO();
        dto.setId(review.getId());
        dto.setTouristName(review.getTouristName());
        dto.setTouristSurname(review.getTouristSurname());
        dto.setRating(review.getRating());
        dto.setComment(review.getComment());
        dto.setTouristId(review.getTouristId());
        dto.setVisitDate(review.getVisitDate());
        dto.setCommentDate(review.getCommentDate());
        dto.setImages(review.getImages());
        return dto;
    }
}

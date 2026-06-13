package com.example.purchase_service.repo;

import com.example.purchase_service.model.TourPurchaseToken;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface TourPurchaseTokenRepository extends MongoRepository<TourPurchaseToken, String> {
    List<TourPurchaseToken> findByTouristId(Long touristId);
    boolean existsByTouristIdAndTourId(Long touristId, Long tourId);
}

package com.example.purchase_service.repo;

import com.example.purchase_service.model.ShoppingCart;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface ShoppingCartRepository extends MongoRepository<ShoppingCart, Long> {
    Optional<ShoppingCart> findById(Long id);
    Optional<ShoppingCart> findByTouristId(Long id);
}

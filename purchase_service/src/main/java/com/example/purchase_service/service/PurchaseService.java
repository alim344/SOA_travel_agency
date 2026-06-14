package com.example.purchase_service.service;

import com.example.purchase_service.grpc.TourServiceGrpcClient;
import com.example.purchase_service.model.OrderItem;
import com.example.purchase_service.model.ShoppingCart;
import com.example.purchase_service.model.TourPurchaseToken;
import com.example.purchase_service.repo.ShoppingCartRepository;
import com.example.purchase_service.repo.TourPurchaseTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import io.grpc.StatusRuntimeException;
import com.example.tour_service.proto.GetTourByIdRequest;
import com.example.tour_service.proto.TourResponse;
import com.example.tour_service.proto.TourServiceGrpc;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PurchaseService {

    private final ShoppingCartRepository cartRepository;
    private final TourPurchaseTokenRepository tokenRepository;
    private final TourServiceGrpcClient tourClient;

    public ShoppingCart addToCart(Long touristId, Long tourId) {

        TourResponse tour;
        try {
            tour = tourClient.getTourById(tourId);
        } catch (StatusRuntimeException e) {
            throw new RuntimeException("Tour not found.");
        }

        if ("ARCHIVED".equalsIgnoreCase(tour.getStatus())) {
            throw new RuntimeException("This tour is not available for purchase.");

        }

        if (!"PUBLISHED".equalsIgnoreCase(tour.getStatus())) {
            throw new RuntimeException("This tour is not available for purchase.");
        }

        if (tokenRepository.existsByTouristIdAndTourId(touristId, tourId)) {
            throw new RuntimeException("You have already purchased this tour.");
        }

        ShoppingCart cart = cartRepository.findByTouristId(touristId)
                .orElse(ShoppingCart.builder()
                        .touristId(touristId)
                        .items(new ArrayList<>())
                        .totalPrice(0.0)
                        .build());

        boolean alreadyInCart = cart.getItems().stream()
                .anyMatch(item -> item.getTourId().equals(tourId));
        if (alreadyInCart) {
            throw new RuntimeException("This tour is already in your cart.");
        }

        OrderItem newItem = OrderItem.builder()
                .tourId(tourId)
                .tourName(tour.getName())
                .price(tour.getPrice())
                .build();

        cart.getItems().add(newItem);
        cart.setTotalPrice(cart.getItems().stream()
                .mapToDouble(OrderItem::getPrice)
                .sum());

        return cartRepository.save(cart);
    }

    public ShoppingCart removeFromCart(Long touristId, Long tourId) {
        ShoppingCart cart = cartRepository.findByTouristId(touristId)
                .orElseThrow(() -> new RuntimeException("Cart not found."));

        cart.getItems().removeIf(item -> item.getTourId().equals(tourId));
        cart.setTotalPrice(cart.getItems().stream()
                .mapToDouble(OrderItem::getPrice)
                .sum());

        return cartRepository.save(cart);
    }

    public List<String> checkout(Long touristId) {
        ShoppingCart cart = cartRepository.findByTouristId(touristId)
                .orElseThrow(() -> new RuntimeException("Your cart is empty."));

        if (cart.getItems().isEmpty()) {
            throw new RuntimeException("Your cart is empty.");
        }

        List<String> tokens = new ArrayList<>();

        for (OrderItem item : cart.getItems()) {
            String token = UUID.randomUUID().toString();

            TourPurchaseToken purchaseToken = TourPurchaseToken.builder()
                    .touristId(touristId)
                    .tourId(item.getTourId())
                    .token(token)
                    .build();

            tokenRepository.save(purchaseToken);
            tokens.add(token);
        }

        cart.getItems().clear();
        cart.setTotalPrice(0.0);
        cartRepository.save(cart);

        return tokens;
    }

    public ShoppingCart getCart(Long touristId) {
        return cartRepository.findByTouristId(touristId)
                .orElse(ShoppingCart.builder()
                        .touristId(touristId)
                        .items(new ArrayList<>())
                        .totalPrice(0.0)
                        .build());
    }
}

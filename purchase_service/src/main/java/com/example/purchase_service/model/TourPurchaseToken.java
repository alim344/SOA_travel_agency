package com.example.purchase_service.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "tour_purchase_tokens")
public class TourPurchaseToken {
    @Id
    private String id;

    private Long touristId;
    private Long tourId;
    private String token;

    @Builder.Default
    private LocalDateTime purchasedAt = LocalDateTime.now();
}

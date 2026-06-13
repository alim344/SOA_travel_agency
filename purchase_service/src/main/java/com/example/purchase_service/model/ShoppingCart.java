package com.example.purchase_service.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "shopping_carts")
public class ShoppingCart {
    @Id
    private String id;

    @Indexed(unique = true)
    private Long touristId;

    @Builder.Default
    private List<OrderItem> items = new ArrayList<>();

    private double totalPrice;
}

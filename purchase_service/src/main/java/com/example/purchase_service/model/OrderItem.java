package com.example.purchase_service.model;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderItem {
    private Long tourId;
    private String tourName;
    private double price;
}

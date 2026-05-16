package com.example.tour_service.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReviewDTO {
    private Long id;
    private String touristName;     
    private String touristSurname;
    private int rating;
    private String comment;
    private Long touristId;
    private LocalDateTime visitDate;
    private LocalDateTime commentDate;
    private List<String> images;
}

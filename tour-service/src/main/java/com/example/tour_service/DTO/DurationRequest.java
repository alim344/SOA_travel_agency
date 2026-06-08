package com.example.tour_service.DTO;

import com.example.tour_service.model.TransportType;
import lombok.Data;

@Data
public class DurationRequest {
    private TransportType transportType;
    private int minutes;
}

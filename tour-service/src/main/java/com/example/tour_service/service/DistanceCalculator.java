package com.example.tour_service.service;


import com.example.tour_service.model.KeyPoint;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DistanceCalculator {
    private static final int EARTH_RADIUS_KM = 6371;

    /**
     * Izračunava rastojanje između dve tačke na Zemlji (Haversine formula)
     */
    public double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);

        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return EARTH_RADIUS_KM * c;
    }

    /**
     * Izračunava ukupnu dužinu ture na osnovu liste ključnih tačaka
     */
    public double calculateTotalDistance(List<KeyPoint> keyPoints) {
        if (keyPoints == null || keyPoints.size() < 2) {
            return 0.0;
        }

        double total = 0.0;
        for (int i = 0; i < keyPoints.size() - 1; i++) {
            KeyPoint current = keyPoints.get(i);
            KeyPoint next = keyPoints.get(i + 1);
            total += calculateDistance(
                    current.getLatitude(), current.getLongitude(),
                    next.getLatitude(), next.getLongitude()
            );
        }
        return total;
    }
}

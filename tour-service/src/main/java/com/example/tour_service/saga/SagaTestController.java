package com.example.tour_service.saga;

import com.example.purchase_service.proto.RemoveArchivedTourResponse;
import com.example.tour_service.grpc.PurchaseServiceGrpcClient;
import com.example.tour_service.model.Tour;
import com.example.tour_service.model.TourStatus;
import com.example.tour_service.repo.TourRepository;
import com.example.tour_service.service.TourService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/saga-test")
public class SagaTestController {


    @Autowired
    private TourService tourService;

    @Autowired
    private TourRepository tourRepository;

    @Autowired
    private PurchaseServiceGrpcClient purchaseServiceClient;

    @PostMapping("/run/{tourId}")
    public ResponseEntity<String> runSaga(
            @PathVariable Long tourId,
            @RequestParam(defaultValue = "false") boolean simulirajGresku) {

        System.out.println("\n=== POKRETANJE SAGA TOKA MEHANIZMA ===");

        try {
            tourService.archiveTour(tourId);
            System.out.println("[SAGA OKRESTRATOR] Korak 1 uspešan: Tura " + tourId + " uspešno arhivirana u bazi.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("SAGA nije ni počela: " + e.getMessage());
        }

        boolean purchaseServiceStepSuccess = false;
        String errorMessage = "";

        if (simulirajGresku) {
            purchaseServiceStepSuccess = false;
            errorMessage = "gRPC Connection Timeout: PurchaseService je nedostupan (Simulirani pad).";
            System.out.println("[SAGA OKRESTRATOR] Korak 2: Detektovana greška u komunikaciji sa PurchaseService!");
        } else {
            try {
                RemoveArchivedTourResponse response = purchaseServiceClient.removeTourFromCarts(tourId);
                purchaseServiceStepSuccess = response.getSuccess();
                errorMessage = response.getMessage();
                System.out.println("[SAGA OKRESTRATOR] Korak 2 uspešan: gRPC javlja da su korpe očišćene.");
            } catch (Exception e) {
                purchaseServiceStepSuccess = false;
                errorMessage = e.getMessage();
                System.out.println("[SAGA OKRESTRATOR] Korak 2 neuspešan: gRPC poziv bacio izuzetak.");
            }
        }

        if (!purchaseServiceStepSuccess) {
            System.err.println("[SAGA OKRESTRATOR] Korak 3: POKREĆEM KOMPENZACIONU TRANSAKCIJU...");

            Tour tour = tourRepository.findById(tourId).orElse(null);
            if (tour != null) {
                tour.setStatus(TourStatus.PUBLISHED);
                tour.setArchivedAt(null);
                tourRepository.save(tour);
                System.err.println("[SAGA OKRESTRATOR] Kompenzacija uspešna: Tura " + tourId + " vraćena u status PUBLISHED.");
            }

            return ResponseEntity.status(500).body("SAGA Prekinuta (Rollback izvršen): Čišćenje korpi propalo -> " + errorMessage);
        }

        System.out.println("=== SAGA TOK USPEŠNO ZAVRŠEN ZA TURU " + tourId + " ===");
        return ResponseEntity.ok("SAGA uspešna! Tura arhivirana i sve korpe ažurirane.");
    }
}

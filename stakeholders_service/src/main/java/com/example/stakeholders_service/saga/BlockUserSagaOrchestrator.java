package com.example.stakeholders_service.saga;

import com.example.stakeholders_service.service.AdminService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class BlockUserSagaOrchestrator {
    @Autowired
    private AdminService adminService;

    @Autowired
    private RestTemplate restTemplate;

    @Value("${tour.service.url}")
    private String tourServiceUrl;

    public boolean blockUserSaga(Long userId, boolean simulateFail) {
        System.out.println("\n=== POKRETANJE SAGA - BLOKIRANJE KORISNIKA ===");

        boolean blocked = adminService.blockUser(userId);
        if (!blocked) return false;
        System.out.println("[SAGA ORKESTRATOR] Korak 1 uspešan: Korisnik " + userId + " blokiran.");

        try {
            if (simulateFail) {
                throw new RuntimeException("Simulirani pad tour-service-a");
            }

            String url = tourServiceUrl + "/tour/archiveByUser/" + userId;
            restTemplate.exchange(url, HttpMethod.PUT, null, String.class);
            System.out.println("[SAGA ORKESTRATOR] Korak 2 uspešan: Ture korisnika id:" + userId + " arhivirane.");
            System.out.println("=== SAGA USPEŠNO ZAVRŠENA ===");
            return true;

        } catch (Exception e) {
            System.err.println("[SAGA ORKESTRATOR] Korak 2 neuspešan: " + e.getMessage());
            System.err.println("[SAGA ORKESTRATOR] Korak 3: POKREĆEM KOMPENZACIONU TRANSAKCIJU...");
            adminService.unblockUser(userId);
            System.err.println("[SAGA ORKESTRATOR] Kompenzacija uspešna: Korisnik " + userId + " odblokiran.");
            throw new RuntimeException("SAGA prekinuta (Rollback izvršen): " + e.getMessage());
        }
    }

}

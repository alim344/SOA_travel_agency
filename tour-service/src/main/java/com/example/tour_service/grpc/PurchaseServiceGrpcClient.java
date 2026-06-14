package com.example.tour_service.grpc;

import com.example.purchase_service.proto.*;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
@Component
public class PurchaseServiceGrpcClient {

    @Value("${purchase.grpc.host:purchase-service}")
    private String host;

    @Value("${purchase.grpc.port:9093}")
    private int port;

    private ManagedChannel channel;
    private PurchaseServiceGrpc.PurchaseServiceBlockingStub stub;

    @PostConstruct
    public void init() {
        channel = ManagedChannelBuilder.forAddress(host, port)
                .usePlaintext()
                .build();
        stub = PurchaseServiceGrpc.newBlockingStub(channel);
    }

    @PreDestroy
    public void shutdown() {
        if (channel != null && !channel.isShutdown()) {
            channel.shutdown();
        }
    }

    public boolean isTourPurchased(Long touristId, Long tourId) {
        IsTourPurchasedRequest request = IsTourPurchasedRequest.newBuilder()
                .setTouristId(touristId)
                .setTourId(tourId)
                .build();

        IsTourPurchasedResponse response = stub.isTourPurchased(request);
        return response.getIsPurchased();
    }

    public RemoveArchivedTourResponse removeTourFromCarts(Long tourId) {
        RemoveArchivedTourRequest request = RemoveArchivedTourRequest.newBuilder()
                .setTourId(tourId)
                .build();

        return stub.removeArchivedTourFromCarts(request);
    }
}

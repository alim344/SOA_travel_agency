package com.example.purchase_service.grpc;

import com.example.tour_service.proto.*;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class TourServiceGrpcClient {

    @Value("${tour.grpc.host:tour-service}")
    private String host;

    @Value("${tour.grpc.port:9090}")
    private int port;

    private ManagedChannel channel;
    private TourServiceGrpc.TourServiceBlockingStub stub;

    @PostConstruct
    public void init() {
        channel = ManagedChannelBuilder.forAddress(host, port)
                .usePlaintext()
                .build();
        stub = TourServiceGrpc.newBlockingStub(channel);
    }

    @PreDestroy
    public void shutdown() {
        if (channel != null && !channel.isShutdown()) {
            channel.shutdown();
        }
    }

    public TourResponse getTourById(Long tourId) {
        GetTourByIdRequest request = GetTourByIdRequest.newBuilder()
                .setId(tourId)
                .build();
        return stub.getTourById(request);
    }

    public List<TourResponse> getToursByIds(List<Long> tourIds) {
        GetToursByIdsRequest request = GetToursByIdsRequest.newBuilder()
                .addAllIds(tourIds)
                .build();

        ToursListResponse response = stub.getToursByIds(request);
        return response.getToursList();
    }
}

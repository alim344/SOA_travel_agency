package com.example.tour_service.grpc;

import com.example.tour_service.DTO.TourDTO;
import com.example.tour_service.proto.*;
import com.example.tour_service.service.TourService;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@GrpcService
public class TourGrpcService extends TourServiceGrpc.TourServiceImplBase {
    @Autowired
    private TourService tourService;

    @Override
    public void getTourById(GetTourByIdRequest request,
                            StreamObserver<TourResponse> responseObserver) {

        TourDTO dto = tourService.getTourById(request.getId());

        TourResponse response = TourResponse.newBuilder()
                .setId(dto.getId())
                .setName(dto.getName())
                .setDescription(dto.getDescription())
                .setDifficulty(dto.getDifficulty())
                .addAllTags(dto.getTags() != null ? dto.getTags() : List.of())
                .setStatus(dto.getStatus().name())
                .setPrice(dto.getPrice())
                .setAuthorId(dto.getAuthorId())
                .setTotalDistance(dto.getTotalDistance())
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void createTour(CreateTourRequest request,
                           StreamObserver<CreateTourResponse> responseObserver) {

        TourDTO dto = new TourDTO();
        dto.setName(request.getName());
        dto.setDescription(request.getDescription());
        dto.setDifficulty(request.getDifficulty());
        dto.setTags(request.getTagsList());
        dto.setAuthorId(request.getAuthorId());

        tourService.createTour(dto, request.getAuthorId());

        CreateTourResponse response = CreateTourResponse.newBuilder()
                .setMessage("Successful!")
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}

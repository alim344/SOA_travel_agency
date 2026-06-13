package com.example.tour_service.grpc;

import com.example.tour_service.DTO.LocationDTO;
import com.example.tour_service.DTO.TourExecutionDTO;
import com.example.tour_service.proto.CheckPositionRequest;
import com.example.tour_service.proto.TourExecutionResponse;
import com.example.tour_service.proto.TourExecutionServiceGrpc;
import com.example.tour_service.service.TourExecutionService;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.Map;

@GrpcService
public class TourExecutionGrpcServiceImpl extends TourExecutionServiceGrpc.TourExecutionServiceImplBase {

    @Autowired
    private TourExecutionService tourExecutionService;

    @Override
    public void checkPosition(CheckPositionRequest request, StreamObserver<TourExecutionResponse> responseObserver) {
        try {

            LocationDTO locationDTO = new LocationDTO();
            locationDTO.setLatitude(request.getLatitude());
            locationDTO.setLongitude(request.getLongitude());

            TourExecutionDTO dto = tourExecutionService.checkPosition(request.getExecutionId(), locationDTO);

            if (dto != null) {
                Map<Long, String> protoMap = new HashMap<>();
                if (dto.getCompletedKeyPointsWithTime() != null) {
                    dto.getCompletedKeyPointsWithTime().forEach((key, value) -> {
                        protoMap.put(key, value != null ? value.toString() : "");
                    });
                }

                TourExecutionResponse response = TourExecutionResponse.newBuilder()
                        .setExecutionId(dto.getId())
                        .setTouristId(dto.getTouristId())
                        .setTourId(dto.getTourId())
                        .setStatus(dto.getStatus())
                        .setStartTime(dto.getStartTime() != null ? dto.getStartTime().toString() : "")
                        .setEndTime(dto.getEndTime() != null ? dto.getEndTime().toString() : "")
                        .setLastActivityDateTime(dto.getLastActivityDateTime() != null ? dto.getLastActivityDateTime().toString() : "")
                        .setLastLatitude(dto.getLastLatitude())
                        .setLastLongitude(dto.getLastLongitude())
                        .putAllCompletedKeyPointsWithTime(protoMap)
                        .build();


                responseObserver.onNext(response);
                responseObserver.onCompleted();
            } else {

                responseObserver.onError(io.grpc.Status.NOT_FOUND
                        .withDescription("Sesija sa ID-jem " + request.getExecutionId() + " nema.")
                        .asRuntimeException());
            }
        } catch (Exception e) {
            responseObserver.onError(io.grpc.Status.INTERNAL
                    .withDescription("Greskica: " + e.getMessage())
                    .asRuntimeException());
        }
    }

}

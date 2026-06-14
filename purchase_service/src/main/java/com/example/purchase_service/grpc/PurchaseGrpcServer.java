package com.example.purchase_service.grpc;

import com.example.purchase_service.model.OrderItem;
import com.example.purchase_service.model.ShoppingCart;
import com.example.purchase_service.proto.*;
import com.example.purchase_service.service.PurchaseService;
import com.example.purchase_service.proto.OrderItem.Builder;
import com.example.tour_service.proto.TourResponse;
import io.grpc.stub.StreamObserver;
import io.grpc.Status;
import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.server.service.GrpcService;

import java.util.List;

@GrpcService
@RequiredArgsConstructor
public class PurchaseGrpcServer extends PurchaseServiceGrpc.PurchaseServiceImplBase {

    private final PurchaseService purchaseService;

    @Override
    public void addToCart(AddToCartRequest request, StreamObserver<CartResponse> responseObserver) {
        try {
            ShoppingCart cart = purchaseService.addToCart(
                    request.getTouristId(),
                    request.getTourId()
            );
            responseObserver.onNext(toCartResponse(cart));
            responseObserver.onCompleted();
        } catch (RuntimeException e) {
            responseObserver.onError(
                    Status.INVALID_ARGUMENT.withDescription(e.getMessage()).asRuntimeException()
            );
        }
    }

    @Override
    public void removeFromCart(RemoveFromCartRequest request, StreamObserver<CartResponse> responseObserver) {
        try {
            ShoppingCart cart = purchaseService.removeFromCart(
                    request.getTouristId(),
                    request.getTourId()
            );
            responseObserver.onNext(toCartResponse(cart));
            responseObserver.onCompleted();
        } catch (RuntimeException e) {
            responseObserver.onError(
                    Status.NOT_FOUND.withDescription(e.getMessage()).asRuntimeException()
            );
        }
    }

    @Override
    public void getCart(GetCartRequest request, StreamObserver<CartResponse> responseObserver) {
        try {
            ShoppingCart cart = purchaseService.getCart(request.getTouristId());
            responseObserver.onNext(toCartResponse(cart));
            responseObserver.onCompleted();
        } catch (RuntimeException e) {
            responseObserver.onError(
                    Status.NOT_FOUND.withDescription(e.getMessage()).asRuntimeException()
            );
        }
    }

    @Override
    public void checkout(CheckoutRequest request, StreamObserver<CheckoutResponse> responseObserver) {
        try {
            List<String> tokens = purchaseService.checkout(request.getTouristId());
            CheckoutResponse response = CheckoutResponse.newBuilder()
                    .addAllTokens(tokens)
                    .build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (RuntimeException e) {
            responseObserver.onError(
                    Status.FAILED_PRECONDITION.withDescription(e.getMessage()).asRuntimeException()
            );
        }
    }

    private CartResponse toCartResponse(ShoppingCart cart) {
        CartResponse.Builder builder = CartResponse.newBuilder()
                .setTotalPrice(cart.getTotalPrice());

        for (OrderItem item : cart.getItems()) {
            com.example.purchase_service.proto.OrderItem protoItem =
                    com.example.purchase_service.proto.OrderItem.newBuilder()
                            .setTourId(item.getTourId())
                            .setTourName(item.getTourName())
                            .setPrice(item.getPrice())
                            .build();
            builder.addItems(protoItem);
        }

        return builder.build();
    }


    @Override
    public void getToursForTourist(GetToursForTouristRequest request, StreamObserver<TouristToursResponse> responseObserver) {
        try {
            List<TourResponse> tours = purchaseService.getToursForTourist(request.getTouristId());

            TouristToursResponse.Builder responseBuilder = TouristToursResponse.newBuilder();

            for (TourResponse tour : tours) {
                PurchasedTourResponse purchasedTour = PurchasedTourResponse.newBuilder()
                        .setId(tour.getId())
                        .setName(tour.getName())
                        .setDescription(tour.getDescription())
                        .setDifficulty(tour.getDifficulty())
                        .addAllTags(tour.getTagsList())
                        .setStatus(tour.getStatus())
                        .setPrice(tour.getPrice())
                        .setAuthorId(tour.getAuthorId())
                        .setTotalDistance(tour.getTotalDistance())
                        .build();

                responseBuilder.addTours(purchasedTour);
            }

            responseObserver.onNext(responseBuilder.build());
            responseObserver.onCompleted();

        } catch (Exception e) {
            responseObserver.onError(io.grpc.Status.INTERNAL
                    .withDescription("greskica: " + e.getMessage())
                    .asRuntimeException());
        }
    }

    @Override
    public void isTourPurchased(IsTourPurchasedRequest request, StreamObserver<IsTourPurchasedResponse> responseObserver) {
        try {
            boolean purchased = purchaseService.isTourPurchased(request.getTouristId(), request.getTourId());

            IsTourPurchasedResponse response = IsTourPurchasedResponse.newBuilder()
                    .setIsPurchased(purchased)
                    .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(Status.INTERNAL
                    .withDescription("Greška pri proveri kupovine: " + e.getMessage())
                    .asRuntimeException());
        }
    }

}

package com.example.purchase_service.grpc;

import com.example.purchase_service.model.OrderItem;
import com.example.purchase_service.model.ShoppingCart;
import com.example.purchase_service.service.PurchaseService;
import com.example.purchase_service.proto.AddToCartRequest;
import com.example.purchase_service.proto.CartResponse;
import com.example.purchase_service.proto.CheckoutRequest;
import com.example.purchase_service.proto.CheckoutResponse;
import com.example.purchase_service.proto.OrderItem.Builder;
import com.example.purchase_service.proto.PurchaseServiceGrpc;
import com.example.purchase_service.proto.RemoveFromCartRequest;
import com.example.purchase_service.service.PurchaseService;
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
}

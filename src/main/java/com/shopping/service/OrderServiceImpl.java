package com.shopping.service;

import com.google.protobuf.util.Timestamps;
import com.shopping.db.Order;
import com.shopping.db.Orderdao;
import com.shopping.stubs.order.OrderRequest;
import com.shopping.stubs.order.OrderResponse;
import com.shopping.stubs.order.OrderServiceGrpc;
import io.grpc.stub.StreamObserver;

import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class OrderServiceImpl extends OrderServiceGrpc.OrderServiceImplBase {
    private static final Logger logger = Logger.getLogger(OrderServiceImpl.class.getName());

    private Orderdao orderdao = new Orderdao();
    @Override
    public void getOrdersForUser(OrderRequest request, StreamObserver<OrderResponse> responseObserver) {
        List<Order> orders = orderdao.getOrders(request.getUserId());
        logger.info("Got orders from OrderDao and converting to OrderResponse proto objects");

        List<com.shopping.stubs.order.Order> ordersForUser = orders.stream().map(o -> com.shopping.stubs.order.Order.newBuilder().setUserId(o.getUserId())
                .setOrderId(o.getOrderId()).setNoOfItems(o.getNoOfItems()).setTotalAmount(o.getTotalAmount())
                .setOrderDate(Timestamps.fromMillis(o.getOrderDate().getTime())).build()).collect(Collectors.toList());

        OrderResponse orderResponse = OrderResponse.newBuilder().addAllOrder(ordersForUser).build();
        responseObserver.onNext(orderResponse);
        responseObserver.onCompleted();
    }
}

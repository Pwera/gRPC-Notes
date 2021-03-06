package me.piotr.wera.customer.client;

import com.google.common.collect.Streams;
import com.google.common.util.concurrent.Uninterruptibles;
import io.grpc.ManagedChannel;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;
import io.opencensus.trace.Status;
import me.piotr.wera.common.GrpcCommons;
import me.piotr.wera.customer.CustomerRequest;
import me.piotr.wera.customer.CustomerResponse;
import me.piotr.wera.customer.CustomerServiceGrpc;

import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

public class CustomerClient implements GrpcCommons {

    private static int messageID = 0;
    public static void main(String[] args) {
        new CustomerClient().go();
    }

    public void go() {
        System.out.println("Starting gRPC Client");
        final ManagedChannel channel = createChannel(USE_SSL);

        final CustomerServiceGrpc.CustomerServiceBlockingStub syncClient = CustomerServiceGrpc.newBlockingStub(channel);

        System.out.println("Calling gRPC Unary Method");
        try {
            Stream.of(
                    syncClient.withDeadline(deadline)
                            .myServiceUnary(constructRequest())).forEach(this::responseFromServer);
        } catch (StatusRuntimeException e) {
            if (Status.INVALID_ARGUMENT.equals(e.getStatus())) {
                System.out.println("INVALID_ARGUMENT error");
            } else if (Status.DEADLINE_EXCEEDED.equals(e.getStatus())) {
                System.out.println("DEADLINE_EXCEEDED error");
            } else {
                e.printStackTrace();
            }
        }


        System.out.println("Calling gRPC Server Streaming Method");
        Streams.stream(syncClient.myServiceServerStreaming(constructRequest())).forEach(this::responseFromServer);

        Uninterruptibles.sleepUninterruptibly(1000L, TimeUnit.MILLISECONDS);
        System.out.println("Calling gRPC Client Streaming Method");
        final CustomerServiceGrpc.CustomerServiceStub customerServiceStub = CustomerServiceGrpc.newStub(channel);
        final StreamObserver<CustomerRequest> customerRequestStreamObserver = customerServiceStub.myServiceClientStreaming(new CustomerResponseStreamObserver());
        customerRequestStreamObserver.onNext(constructRequest());
        customerRequestStreamObserver.onNext(constructRequest());
        customerRequestStreamObserver.onNext(constructRequest());
        customerRequestStreamObserver.onCompleted();

        Uninterruptibles.sleepUninterruptibly(1000L, TimeUnit.MILLISECONDS);
        System.out.println("Calling gRPC BiDi Streaming Method");
        final StreamObserver<CustomerRequest> customerRequestBiDiObserver = customerServiceStub.myServiceClientStreaming(new CustomerResponseStreamObserver());
        customerRequestBiDiObserver.onNext(constructRequest());
        customerRequestBiDiObserver.onNext(constructRequest());
        customerRequestBiDiObserver.onNext(constructRequest());
        customerRequestBiDiObserver.onCompleted();


        System.out.println("Shutting down channel");
        Uninterruptibles.sleepUninterruptibly(4000L, TimeUnit.MILLISECONDS);
        channel.shutdown();

    }

    private CustomerRequest constructRequest() {
        return CustomerRequest
                .newBuilder()
                .setValue(messageID % 2 == 0 ? "expected" : "not-exxpected")
                .setId(1)
                .build();
    }

    private void responseFromServer(CustomerResponse customerResponse) {
        System.out.println("Response from server: " + customerResponse.getStatus() + " " + customerResponse.getId());

    }

    private class CustomerResponseStreamObserver implements StreamObserver<CustomerResponse> {
        @Override
        public void onNext(CustomerResponse value) {
            Stream.of("Received message from server ", value).forEach(System.out::println);
        }

        @Override
        public void onError(Throwable t) {

        }

        @Override
        public void onCompleted() {

        }
    }


}

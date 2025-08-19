package com.food.ordering.system.kafka.stream.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentRequestAvroModel {
    
    @JsonProperty("id")
    private UUID id;
    
    @JsonProperty("sagaId")
    private UUID sagaId;
    
    @JsonProperty("customerId")
    private UUID customerId;
    
    @JsonProperty("orderId")
    private UUID orderId;
    
    @JsonProperty("price")
    private BigDecimal price;
    
    @JsonProperty("createdAt")
    private Instant createdAt;
    
    @JsonProperty("paymentOrderStatus")
    private PaymentOrderStatus paymentOrderStatus;
    
    public enum PaymentOrderStatus {
        PENDING, CANCELLED
    }
    
    public static PaymentRequestAvroModel createPaymentRequest(
            UUID sagaId, UUID customerId, UUID orderId, BigDecimal price) {
        return PaymentRequestAvroModel.builder()
                .id(UUID.randomUUID())
                .sagaId(sagaId)
                .customerId(customerId)
                .orderId(orderId)
                .price(price)
                .createdAt(Instant.now())
                .paymentOrderStatus(PaymentOrderStatus.PENDING)
                .build();
    }
}

package com.food.ordering.system.kafka.stream.consumer.listener;

import com.food.ordering.system.kafka.stream.consumer.PaymentRequestKafkaStreamConsumer;
import com.food.ordering.system.kafka.stream.model.PaymentRequestAvroModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class PaymentRequestKafkaStreamConsumerImpl implements PaymentRequestKafkaStreamConsumer {

    @Override
    @KafkaListener(topics = "${spring.kafka.topic.payment-request}", groupId = "${spring.kafka.consumer.group-id}")
    public void receive(@Payload List<PaymentRequestAvroModel> messages) {
        log.info("Received {} payment request messages from Kafka", messages.size());
        
        for (PaymentRequestAvroModel message : messages) {
            log.info("Processing payment request: {}", message);
            
            // Process the payment request
            processPaymentRequest(message);
        }
    }
    
    private void processPaymentRequest(PaymentRequestAvroModel paymentRequest) {
        log.info("Processing payment request: {}", paymentRequest.getId());
        
        switch (paymentRequest.getPaymentOrderStatus()) {
            case PENDING:
                log.info("Processing pending payment for order: {}", paymentRequest.getOrderId());
                // Aquí iría la lógica de procesamiento de pago
                break;
            case CANCELLED:
                log.info("Payment cancelled for order: {}", paymentRequest.getOrderId());
                break;
            default:
                log.warn("Unknown payment order status: {}", paymentRequest.getPaymentOrderStatus());
        }
    }
}

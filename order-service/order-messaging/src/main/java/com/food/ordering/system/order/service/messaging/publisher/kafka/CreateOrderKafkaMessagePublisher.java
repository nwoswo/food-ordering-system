package com.food.ordering.system.order.service.messaging.publisher.kafka;

import com.food.ordering.system.kafka.stream.model.PaymentRequestModel;
import com.food.ordering.system.kafka.producer.KafkaMessageHelper;
import com.food.ordering.system.kafka.producer.KafkaProducer;
import com.food.ordering.system.order.service.application.config.OrderServiceConfigData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.UUID;

@Component
public class CreateOrderKafkaMessagePublisher {
    
    private static final Logger log = LoggerFactory.getLogger(CreateOrderKafkaMessagePublisher.class);

    private final OrderServiceConfigData orderServiceConfigData;
    private final KafkaProducer<String, PaymentRequestModel> kafkaProducer;
    private final KafkaMessageHelper orderKafkaMessageHelper;

    public CreateOrderKafkaMessagePublisher(OrderServiceConfigData orderServiceConfigData,
                                            KafkaProducer<String, PaymentRequestModel> kafkaProducer,
                                            KafkaMessageHelper kafkaMessageHelper) {
        this.orderServiceConfigData = orderServiceConfigData;
        this.kafkaProducer = kafkaProducer;
        this.orderKafkaMessageHelper = kafkaMessageHelper;
    }

    public void publish(String orderId, String customerId, BigDecimal price) {
        log.info("Publishing order creation event for order id: {}", orderId);

        try {
            PaymentRequestModel paymentRequestModel = PaymentRequestModel.createPaymentRequest(
                    UUID.randomUUID().toString(), // sagaId
                    customerId,
                    orderId,
                    price
            );

            kafkaProducer.send(orderServiceConfigData.getPaymentRequestTopicName(),
                    orderId,
                    paymentRequestModel,
                    orderKafkaMessageHelper);

            log.info("PaymentRequestModel sent to Kafka for order id: {}", paymentRequestModel.getOrderId());
        } catch (Exception e) {
           log.error("Error while sending PaymentRequestModel message" +
                   " to kafka with order id: {}, error: {}", orderId, e.getMessage());
        }
    }
}

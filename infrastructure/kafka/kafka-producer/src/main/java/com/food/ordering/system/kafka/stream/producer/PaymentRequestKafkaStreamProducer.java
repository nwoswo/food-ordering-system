package com.food.ordering.system.kafka.stream.producer;

import com.food.ordering.system.kafka.stream.model.PaymentRequestAvroModel;

public interface PaymentRequestKafkaStreamProducer {
    void send(String topicName, String key, PaymentRequestAvroModel message);
}

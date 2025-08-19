package com.food.ordering.system.kafka.stream.producer;

import com.food.ordering.system.kafka.stream.model.PaymentResponseAvroModel;

public interface PaymentResponseKafkaStreamProducer {
    void send(String topicName, String key, PaymentResponseAvroModel message);
}

package com.food.ordering.system.kafka.stream.producer;

import com.food.ordering.system.kafka.stream.model.RestaurantApprovalResponseAvroModel;

public interface RestaurantApprovalResponseKafkaStreamProducer {
    void send(String topicName, String key, RestaurantApprovalResponseAvroModel message);
}

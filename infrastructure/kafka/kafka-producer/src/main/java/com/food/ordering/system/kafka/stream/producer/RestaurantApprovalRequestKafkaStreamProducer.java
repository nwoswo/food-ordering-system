package com.food.ordering.system.kafka.stream.producer;

import com.food.ordering.system.kafka.stream.model.RestaurantApprovalRequestAvroModel;

public interface RestaurantApprovalRequestKafkaStreamProducer {
    void send(String topicName, String key, RestaurantApprovalRequestAvroModel message);
}

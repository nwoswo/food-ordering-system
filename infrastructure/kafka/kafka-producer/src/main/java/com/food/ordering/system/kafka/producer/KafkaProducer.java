package com.food.ordering.system.kafka.producer;

import org.springframework.kafka.support.SendResult;

public interface KafkaProducer<K, V> {
    void send(String topicName, K key, V message, KafkaMessageHelper kafkaMessageHelper);
    void send(String topicName, K key, V message, 
             org.springframework.util.concurrent.ListenableFutureCallback<SendResult<String, V>> callback);
}

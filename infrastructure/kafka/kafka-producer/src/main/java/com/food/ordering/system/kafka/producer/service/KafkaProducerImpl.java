package com.food.ordering.system.kafka.producer.service;

import com.food.ordering.system.kafka.producer.KafkaMessageHelper;
import com.food.ordering.system.kafka.producer.KafkaProducer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import org.springframework.util.concurrent.ListenableFuture;

@Slf4j
@Component
public class KafkaProducerImpl<K, V> implements KafkaProducer<K, V> {

    private final KafkaTemplate<K, V> kafkaTemplate;
    private final KafkaMessageHelper<K, V> kafkaMessageHelper;

    public KafkaProducerImpl(KafkaTemplate<K, V> kafkaTemplate, KafkaMessageHelper<K, V> kafkaMessageHelper) {
        this.kafkaTemplate = kafkaTemplate;
        this.kafkaMessageHelper = kafkaMessageHelper;
    }

    @Override
    public void send(String topicName, K key, V message, KafkaMessageHelper<K, V> kafkaMessageHelper) {
        log.info("Sending message to topic: {} with key: {}, message: {}", topicName, key, message);
        
        try {
            ListenableFuture<SendResult<K, V>> future = kafkaTemplate.send(topicName, key, message);
            future.addCallback(
                result -> log.info("Message sent successfully to topic: {} with key: {}", topicName, key),
                throwable -> log.error("Failed to send message to topic: {} with key: {}, error: {}", 
                    topicName, key, throwable.getMessage())
            );
        } catch (Exception e) {
            log.error("Error while sending message to topic: {} with key: {}, error: {}", 
                topicName, key, e.getMessage());
            throw new RuntimeException("Error while sending message to Kafka", e);
        }
    }
}

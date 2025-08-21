package com.food.ordering.system.kafka.producer.service;

import com.food.ordering.system.kafka.producer.KafkaMessageHelper;
import com.food.ordering.system.kafka.producer.KafkaProducer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFuture;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Component
public class KafkaProducerImpl<K, V> implements KafkaProducer<K, V> {

    private final KafkaTemplate<K, V> kafkaTemplate;
    private final KafkaMessageHelper kafkaMessageHelper;

    public KafkaProducerImpl(KafkaTemplate<K, V> kafkaTemplate, KafkaMessageHelper kafkaMessageHelper) {
        this.kafkaTemplate = kafkaTemplate;
        this.kafkaMessageHelper = kafkaMessageHelper;
    }

    @Override
    @SuppressWarnings("deprecation")
    public void send(String topicName, K key, V message, KafkaMessageHelper kafkaMessageHelper) {
        log.info("Sending message to topic: {} with key: {}, message: {}", topicName, key, message);
        
        try {
            ListenableFuture<SendResult<K, V>> listenableFuture = kafkaTemplate.send(topicName, key, message);
            
            // Use the callback handler directly with lambda expressions
            listenableFuture.addCallback(
                result -> kafkaMessageHelper.handleKafkaCallback(result, null, topicName, message, key.toString(), message.getClass().getSimpleName()),
                ex -> kafkaMessageHelper.handleKafkaCallback(null, ex, topicName, message, key.toString(), message.getClass().getSimpleName())
            );
        } catch (Exception e) {
            log.error("Error while sending message to topic: {} with key: {}, error: {}", 
                topicName, key, e.getMessage());
            throw new RuntimeException("Error while sending message to Kafka", e);
        }
    }
    
    @SuppressWarnings("deprecation")
    public void send(String topicName, K key, V message, 
                    org.springframework.util.concurrent.ListenableFutureCallback<SendResult<String, V>> callback) {
        log.info("Sending message to topic: {} with key: {}, message: {}", topicName, key, message);
        
        try {
            ListenableFuture<SendResult<K, V>> listenableFuture = kafkaTemplate.send(topicName, key, message);
            
            // Create a bridge callback that handles the type conversion
            listenableFuture.addCallback(
                result -> {
                    @SuppressWarnings("unchecked")
                    SendResult<String, V> stringResult = (SendResult<String, V>) result;
                    callback.onSuccess(stringResult);
                },
                callback::onFailure
            );
        } catch (Exception e) {
            log.error("Error while sending message to topic: {} with key: {}, error: {}", 
                topicName, key, e.getMessage());
            throw new RuntimeException("Error while sending message to Kafka", e);
        }
    }
}

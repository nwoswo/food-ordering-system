package com.food.ordering.system.kafka.producer.service;

import com.food.ordering.system.kafka.producer.KafkaMessageHelper;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.util.function.BiConsumer;

@Slf4j
@Component
public class KafkaMessageHelperImpl<K, V> implements KafkaMessageHelper<K, V> {

    @Override
    public ProducerRecord<String, V> getProducerRecord(String topicName, K key, V message, String orderId) {
        return new ProducerRecord<>(topicName, key.toString(), message);
    }

    @Override
    public BiConsumer<SendResult<String, V>, Throwable> getKafkaCallback(String topicName, V avroModel, K orderId, String avroModelName) {
        return (result, throwable) -> {
            if (throwable == null) {
                log.info("Message sent successfully to topic: {} with key: {} and {}: {}", 
                    topicName, orderId, avroModelName, avroModel);
            } else {
                log.error("Error while sending {} message to topic: {} with key: {}, error: {}", 
                    avroModelName, topicName, orderId, throwable.getMessage());
            }
        };
    }
}

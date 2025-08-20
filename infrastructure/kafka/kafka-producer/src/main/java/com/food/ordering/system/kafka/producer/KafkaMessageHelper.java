package com.food.ordering.system.kafka.producer;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.springframework.kafka.support.SendResult;

import java.util.function.BiConsumer;

public interface KafkaMessageHelper<K, V> {
    ProducerRecord<String, V> getProducerRecord(String topicName, K key, V message, String orderId);
    BiConsumer<SendResult<String, V>, Throwable> getKafkaCallback(String topicName, V avroModel, K orderId, String avroModelName);
}

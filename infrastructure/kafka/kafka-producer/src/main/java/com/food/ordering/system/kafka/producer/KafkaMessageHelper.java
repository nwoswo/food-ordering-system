package com.food.ordering.system.kafka.producer;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Component
public class KafkaMessageHelper {

    public <K, V> void handleKafkaCallback(SendResult<K, V> result, Throwable ex, 
                                          String responseTopicName, V messageModel, String orderId, String modelName) {
        if (ex != null) {
            log.error("Error while sending " + modelName +
                    " message {} to topic {}", messageModel.toString(), responseTopicName, ex);
        } else if (result != null) {
            RecordMetadata metadata = result.getRecordMetadata();
            log.info("Received successful response from Kafka for order id: {}" +
                            " Topic: {} Partition: {} Offset: {} Timestamp: {}",
                    orderId,
                    metadata.topic(),
                    metadata.partition(),
                    metadata.offset(),
                    metadata.timestamp());
        }
    }

    public <V> org.springframework.util.concurrent.ListenableFutureCallback<SendResult<String, V>> 
    getKafkaCallback(String responseTopicName, V messageModel, String orderId, String modelName) {
        return new org.springframework.util.concurrent.ListenableFutureCallback<SendResult<String, V>>() {
            @Override
            public void onSuccess(SendResult<String, V> result) {
                handleKafkaCallback(result, null, responseTopicName, messageModel, orderId, modelName);
            }

            @Override
            public void onFailure(Throwable ex) {
                handleKafkaCallback(null, ex, responseTopicName, messageModel, orderId, modelName);
            }
        };
    }
}

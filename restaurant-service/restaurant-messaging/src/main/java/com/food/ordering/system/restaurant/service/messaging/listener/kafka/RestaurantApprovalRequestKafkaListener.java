package com.food.ordering.system.restaurant.service.messaging.listener.kafka;

import com.food.ordering.system.kafka.stream.consumer.KafkaStreamConsumer;
import com.food.ordering.system.kafka.stream.model.RestaurantApprovalRequestModel;
import com.food.ordering.system.restaurant.service.domain.ports.input.message.listener.RestaurantApprovalRequestMessageListener;
import com.food.ordering.system.restaurant.service.messaging.mapper.RestaurantMessagingDataMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class RestaurantApprovalRequestKafkaListener implements KafkaStreamConsumer<RestaurantApprovalRequestModel> {

    private static final Logger log = LoggerFactory.getLogger(RestaurantApprovalRequestKafkaListener.class);

    private final RestaurantApprovalRequestMessageListener restaurantApprovalRequestMessageListener;
    private final RestaurantMessagingDataMapper restaurantMessagingDataMapper;

    public RestaurantApprovalRequestKafkaListener(RestaurantApprovalRequestMessageListener
                                                          restaurantApprovalRequestMessageListener,
                                                  RestaurantMessagingDataMapper
                                                          restaurantMessagingDataMapper) {
        this.restaurantApprovalRequestMessageListener = restaurantApprovalRequestMessageListener;
        this.restaurantMessagingDataMapper = restaurantMessagingDataMapper;
    }

    @Override
    public void receive(List<RestaurantApprovalRequestModel> messages) {
        // This method is required by the interface but not used
        // The actual processing is done by the @KafkaListener method
    }

    @KafkaListener(id = "${kafka-consumer-config.restaurant-approval-consumer-group-id}",
            topics = "${restaurant-service.restaurant-approval-request-topic-name}")
    public void receive(@Payload List<RestaurantApprovalRequestModel> messages,
                        @Header(KafkaHeaders.RECEIVED_MESSAGE_KEY) List<String> keys,
                        @Header(KafkaHeaders.RECEIVED_PARTITION_ID) List<Integer> partitions,
                        @Header(KafkaHeaders.OFFSET) List<Long> offsets) {
        log.info("{} number of orders approval requests received with keys {}, partitions {} and offsets {}" +
                        ", sending for restaurant approval",
                messages.size(),
                keys.toString(),
                partitions.toString(),
                offsets.toString());

        messages.forEach(restaurantApprovalRequestModel -> {
            log.info("Processing order approval for order id: {}", restaurantApprovalRequestModel.getOrderId());
            restaurantApprovalRequestMessageListener.approveOrder(restaurantMessagingDataMapper.
                    restaurantApprovalRequestModelToRestaurantApproval(restaurantApprovalRequestModel));
        });
    }

}

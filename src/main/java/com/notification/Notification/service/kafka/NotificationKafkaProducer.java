package com.notification.Notification.service.kafka;

import com.notification.Notification.dto.NotificationDeliveryDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationKafkaProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void publish(String topic, NotificationDeliveryDTO notification) {
        try {
            CompletableFuture<SendResult<String, Object>> future = kafkaTemplate.send(topic, notification.getUserId().toString(), notification);
            future.whenComplete((result, ex) -> {
                if (ex == null) {
                    log.info("Notification sent to topic [{}] with offset [{}]: {}", 
                            topic, result.getRecordMetadata().offset(), notification);
                } else {
                    log.error("Failed to send notification to topic [{}]: {}", topic, notification, ex);
                    throw new RuntimeException("Failed to send notification to Kafka", ex);
                }
            });
        } catch (Exception e) {
            log.error("Failed to send notification to topic [{}]: {}", topic, notification, e);
            throw new RuntimeException("Failed to send notification to Kafka", e);
        }
    }
}

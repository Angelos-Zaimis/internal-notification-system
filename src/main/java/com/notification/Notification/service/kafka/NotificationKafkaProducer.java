package com.notification.Notification.service.kafka;

import com.notification.Notification.dto.NotificationDeliveryDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import org.springframework.kafka.core.KafkaTemplate;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationKafkaProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void publish(String topic, NotificationDeliveryDTO notification) {
        try {
            kafkaTemplate.send(topic, notification.getUserId().toString(), notification);
            log.info("Notification sent to topic [{}]: {}", topic, notification);
        } catch (Exception e) {
            log.error("Failed to send notification to topic [{}]: {}", topic, notification, e);
        }
    }
}

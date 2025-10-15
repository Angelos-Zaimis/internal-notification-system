package com.notification.Notification.service.kafka;

import com.notification.Notification.configuration.kafka.KafkaGroups;
import com.notification.Notification.configuration.kafka.KafkaTopics;
import com.notification.Notification.dto.InternalNotificationDTO;
import com.notification.Notification.service.notification.NotificationHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class NotificationConsumerService {

    private final NotificationHandler notificationHandler;

    public NotificationConsumerService(@Qualifier("topicNotificationHandler") NotificationHandler notificationHandler) {
        this.notificationHandler = notificationHandler;
    }

    @KafkaListener(topics = KafkaTopics.TOPIC_PUSH_NOTIFICATION, groupId = KafkaGroups.GROUP_NOTIFICATION,
            containerFactory = "notificationListenerFactory")
    @Retryable(
            retryFor = Exception.class,
            maxAttempts = 3,
            backoff = @Backoff(delay = 2000, multiplier = 2)
    )
    public void consumeTopicNotification(InternalNotificationDTO notification) {
        log.info("Received topic notification from sender id: {}", notification.getSenderId());
        notificationHandler.processNotification(notification);
    }
}

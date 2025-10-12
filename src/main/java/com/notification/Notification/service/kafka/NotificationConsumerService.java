package com.notification.Notification.service.kafka;

import com.notification.Notification.configuration.kafka.KafkaGroups;
import com.notification.Notification.configuration.kafka.KafkaTopics;
import com.notification.Notification.dto.InternalNotificationDTO;
import com.notification.Notification.service.notification.topic.TopicNotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
@Slf4j
public class NotificationConsumerService {

    private final TopicNotificationService topicNotificationService;

    @KafkaListener(topics = KafkaTopics.TOPIC_PUSH_NOTIFICATION, groupId = KafkaGroups.GROUP_NOTIFICATION,
            containerFactory = "notificationListenerFactory")
    @Retryable(
            retryFor = Exception.class,
            maxAttempts = 3,
            backoff = @Backoff(delay = 2000, multiplier = 2)
    )
    public void consumeTopicNotification(InternalNotificationDTO notification) {
        log.info("Received topic notification from sender id: {}", notification.getSenderId());
        topicNotificationService.processNotification(notification);
    }
}

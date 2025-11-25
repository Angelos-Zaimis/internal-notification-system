package com.notification.Notification.service.kafka;

import com.notification.Notification.configuration.kafka.KafkaGroups;
import com.notification.Notification.configuration.kafka.KafkaTopics;
import com.notification.Notification.dto.NotificationDeliveryDTO;
import com.notification.Notification.repository.NotificationRepository;
import com.notification.Notification.service.sse.SsePushService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationDispatcher {

    private final NotificationRepository notificationRepository;
    private final SsePushService ssePushService;

    @Transactional(rollbackFor = Exception.class)
    @KafkaListener(topics = KafkaTopics.INTERNAL_TOPIC_PUSH_NOTIFICATION, groupId = KafkaGroups.GROUP_INTERNAL_NOTIFICATION_DISPATCHER,
            containerFactory = "notificationInternalDispatcherListenerFactory")
    @Retryable(
            retryFor = Exception.class,
            maxAttempts = 3,
            backoff = @Backoff(delay = 2000, multiplier = 2)
    )
    public void handleInternalTopicNotification(NotificationDeliveryDTO notification) {
        if (notification == null) {
            log.error("Received null notification in dispatcher");
            throw new IllegalArgumentException("Notification cannot be null");
        }
        log.info("Received internal topic notification: {}", notification);

        dispatch(notification);
        updateNotification(notification);
    }

    private void dispatch(NotificationDeliveryDTO notification) {

        try {
            notification.setSendDate(LocalDateTime.now());
            ssePushService.sendToUser(notification.getUserId(), notification);
            log.info("Dispatched notification to user {}", notification.getUserId());
        } catch (Exception e) {
            log.error("Failed to dispatch notification to user {}.", notification.getUserId(), e);
            throw new IllegalArgumentException("Failed to dispatch notification to user " + notification.getUserId(), e);
        }
    }

    private void updateNotification(NotificationDeliveryDTO notification) {
        notificationRepository.findById(notification.getId()).ifPresentOrElse(existing -> {
            existing.setDelivered(true);
            existing.setSendDate(LocalDateTime.now());
            notificationRepository.save(existing);
            log.info("Notification marked as delivered in DB: {}", existing.getId());
        }, () -> log.warn("Notification not found in DB for update: {}", notification.getId()));
    }
}

package com.notification.Notification.service.notification.topic;

import com.notification.Notification.configuration.kafka.KafkaTopics;
import com.notification.Notification.dto.InternalNotificationDTO;
import com.notification.Notification.dto.NotificationDeliveryDTO;
import com.notification.Notification.entity.Notification;
import com.notification.Notification.exception.NotificationAccessException;
import com.notification.Notification.mapper.NotificationMapper;
import com.notification.Notification.repository.NotificationRepository;
import com.notification.Notification.service.kafka.NotificationKafkaProducer;
import com.notification.Notification.service.notification.NotificationHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service("topicNotificationHandler")
@RequiredArgsConstructor
public class TopicNotificationService implements NotificationHandler {

    private final NotificationKafkaProducer notificationKafkaProducer;
    private final NotificationRepository notificationRepository;
    private final NotificationMapper notificationMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void processNotification(InternalNotificationDTO notification) {
        log.info("Processing notification from sender {}", notification.getSenderId());

        try {
            Notification createdNotification = createNotification(notification);

            NotificationDeliveryDTO notificationDeliveryDTO = notificationMapper.toDto(createdNotification);

            notificationKafkaProducer.publish(KafkaTopics.INTERNAL_TOPIC_PUSH_NOTIFICATION, notificationDeliveryDTO);
        } catch (DataAccessException e) {
            throw new NotificationAccessException("Failed to process notification", e);
        }
    }

    private Notification createNotification(InternalNotificationDTO notification) {
        Notification newNotification = notificationMapper.toEntity(notification);
        notificationRepository.saveAndFlush(newNotification);
        return newNotification;
    }
}


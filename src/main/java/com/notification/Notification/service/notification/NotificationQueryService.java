package com.notification.Notification.service.notification;


import com.notification.Notification.exception.NotFoundException;
import com.notification.Notification.service.authentication.TokenHandler;
import com.notification.Notification.dto.NotificationTxDTO;
import com.notification.Notification.entity.Notification;
import com.notification.Notification.exception.NotificationAccessException;
import com.notification.Notification.mapper.NotificationMapper;
import com.notification.Notification.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@EnableScheduling
@Service
public class NotificationQueryService {

    private final NotificationRepository notificationRepository;
    private final NotificationMapper notificationMapper;
    private final TokenHandler tokenHandler;

    public Page<NotificationTxDTO> handleGetAllNotifications(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        UUID userId = UUID.fromString(tokenHandler.getSubject());
        try {
            Page<Notification> unReadNotifications = findAllNotifications(userId, pageable);

            return unReadNotifications.map(notificationMapper::toTxDTO);
        } catch (DataAccessException e) {
            throw new NotificationAccessException("Failed to access notifications list", e);
        }
    }

    private Page<Notification> findAllNotifications(UUID userId, Pageable pageable) {
        return notificationRepository.findAllByUserId(userId, pageable);
    }

    public NotificationTxDTO handleMarkNotificationAsRead(UUID notificationId) {
        Notification notification = findNotification(notificationId);
        notification.setRead(true);
        notificationRepository.save(notification);

        return notificationMapper.toTxDTO(notification);
    }

    private Notification findNotification(UUID notificationId) {
        return notificationRepository.findById(notificationId).orElseThrow(() -> new NotFoundException("Notification with id " + notificationId + " not found"));
    }

    public void handleDeleteNotification(UUID notificationId) {
        Notification notification = findNotification(notificationId);
        notificationRepository.delete(notification);
    }
}

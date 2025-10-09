package com.notification.Notification.repository;

import com.notification.Notification.entity.Notification;
import com.notification.Notification.entity.NotificationCategory;
import com.notification.Notification.entity.NotificationLevel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class NotificationRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private NotificationRepository notificationRepository;

    private UUID userId;
    private UUID anotherUserId;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        anotherUserId = UUID.randomUUID();
    }

    @Test
    @DisplayName("Should find all notifications by user ID")
    void shouldFindAllByUserId() {
        Notification notification1 = createNotification(userId, "First Notification");
        Notification notification2 = createNotification(userId, "Second Notification");
        Notification notification3 = createNotification(anotherUserId, "Another User Notification");

        entityManager.persist(notification1);
        entityManager.persist(notification2);
        entityManager.persist(notification3);
        entityManager.flush();

        Pageable pageable = PageRequest.of(0, 10);
        Page<Notification> result = notificationRepository.findAllByUserId(userId, pageable);

        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getContent())
                .extracting(Notification::getUserId)
                .containsOnly(userId);
    }

    @Test
    @DisplayName("Should return empty page when no notifications for user")
    void shouldReturnEmptyPageWhenNoNotifications() {
        UUID nonExistentUserId = UUID.randomUUID();
        
        Pageable pageable = PageRequest.of(0, 10);
        Page<Notification> result = notificationRepository.findAllByUserId(nonExistentUserId, pageable);

        assertThat(result.getContent()).isEmpty();
        assertThat(result.getTotalElements()).isZero();
    }

    @Test
    @DisplayName("Should respect pagination parameters")
    void shouldRespectPagination() {
        for (int i = 0; i < 15; i++) {
            Notification notification = createNotification(userId, "Notification " + i);
            entityManager.persist(notification);
        }
        entityManager.flush();

        Pageable firstPage = PageRequest.of(0, 10);
        Page<Notification> firstResult = notificationRepository.findAllByUserId(userId, firstPage);

        assertThat(firstResult.getContent()).hasSize(10);
        assertThat(firstResult.getTotalElements()).isEqualTo(15);
        assertThat(firstResult.getTotalPages()).isEqualTo(2);

        Pageable secondPage = PageRequest.of(1, 10);
        Page<Notification> secondResult = notificationRepository.findAllByUserId(userId, secondPage);

        assertThat(secondResult.getContent()).hasSize(5);
    }

    @Test
    @DisplayName("Should save and retrieve notification with all fields")
    void shouldSaveAndRetrieveNotificationWithAllFields() {
        Notification notification = Notification.builder()
                .userId(userId)
                .senderId(UUID.randomUUID())
                .title("Complete Notification")
                .body("This has all fields")
                .resourceId(UUID.randomUUID())
                .actionUrl("/resources/123")
                .category(NotificationCategory.GRADE)
                .level(NotificationLevel.SUCCESS)
                .delivered(true)
                .read(false)
                .retryCount(2)
                .sendDate(LocalDateTime.now())
                .build();

        Notification saved = notificationRepository.save(notification);
        Notification retrieved = notificationRepository.findById(saved.getId()).orElseThrow();

        assertThat(retrieved.getId()).isNotNull();
        assertThat(retrieved.getUserId()).isEqualTo(userId);
        assertThat(retrieved.getTitle()).isEqualTo("Complete Notification");
        assertThat(retrieved.getCategory()).isEqualTo(NotificationCategory.GRADE);
        assertThat(retrieved.getLevel()).isEqualTo(NotificationLevel.SUCCESS);
        assertThat(retrieved.isDelivered()).isTrue();
        assertThat(retrieved.isRead()).isFalse();
        assertThat(retrieved.getRetryCount()).isEqualTo(2);
        assertThat(retrieved.getCreatedAt()).isNotNull();
    }

    @Test
    @DisplayName("Should update notification read status")
    void shouldUpdateNotificationReadStatus() {
        Notification notification = createNotification(userId, "Unread Notification");
        notification.setRead(false);
        
        Notification saved = notificationRepository.save(notification);
        assertThat(saved.isRead()).isFalse();

        saved.setRead(true);
        Notification updated = notificationRepository.save(saved);

        Notification retrieved = notificationRepository.findById(updated.getId()).orElseThrow();
        assertThat(retrieved.isRead()).isTrue();
    }

    @Test
    @DisplayName("Should delete notification")
    void shouldDeleteNotification() {
        Notification notification = createNotification(userId, "To Be Deleted");
        Notification saved = notificationRepository.save(notification);
        
        UUID savedId = saved.getId();
        assertThat(notificationRepository.findById(savedId)).isPresent();

        notificationRepository.delete(saved);

        assertThat(notificationRepository.findById(savedId)).isEmpty();
    }

    private Notification createNotification(UUID userId, String title) {
        return Notification.builder()
                .userId(userId)
                .title(title)
                .body("Test notification body")
                .category(NotificationCategory.MESSAGE)
                .level(NotificationLevel.INFO)
                .delivered(false)
                .read(false)
                .retryCount(3)
                .build();
    }
}


package com.notification.Notification.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class NotificationTest {

    @Test
    @DisplayName("Should create notification with builder")
    void shouldCreateNotificationWithBuilder() {
        UUID userId = UUID.randomUUID();
        UUID senderId = UUID.randomUUID();
        LocalDateTime now = LocalDateTime.now();

        Notification notification = Notification.builder()
                .userId(userId)
                .senderId(senderId)
                .title("Test Title")
                .body("Test Body")
                .category(NotificationCategory.MESSAGE)
                .level(NotificationLevel.INFO)
                .delivered(false)
                .read(false)
                .retryCount(3)
                .createdAt(now)
                .build();

        assertThat(notification).isNotNull();
        assertThat(notification.getUserId()).isEqualTo(userId);
        assertThat(notification.getSenderId()).isEqualTo(senderId);
        assertThat(notification.getTitle()).isEqualTo("Test Title");
        assertThat(notification.getBody()).isEqualTo("Test Body");
        assertThat(notification.getCategory()).isEqualTo(NotificationCategory.MESSAGE);
        assertThat(notification.getLevel()).isEqualTo(NotificationLevel.INFO);
        assertThat(notification.isDelivered()).isFalse();
        assertThat(notification.isRead()).isFalse();
        assertThat(notification.getRetryCount()).isEqualTo(3);
    }

    @Test
    @DisplayName("Should update notification fields")
    void shouldUpdateNotificationFields() {
        Notification notification = Notification.builder()
                .userId(UUID.randomUUID())
                .title("Original Title")
                .category(NotificationCategory.MESSAGE)
                .level(NotificationLevel.INFO)
                .delivered(false)
                .read(false)
                .retryCount(3)
                .build();

        notification.setTitle("Updated Title");
        notification.setRead(true);
        notification.setDelivered(true);
        notification.setRetryCount(0);

        assertThat(notification.getTitle()).isEqualTo("Updated Title");
        assertThat(notification.isRead()).isTrue();
        assertThat(notification.isDelivered()).isTrue();
        assertThat(notification.getRetryCount()).isZero();
    }

    @Test
    @DisplayName("Should handle translation args as JSON")
    void shouldHandleTranslationArgs() {
        Map<String, Object> translationArgs = new HashMap<>();
        translationArgs.put("userName", "John Doe");
        translationArgs.put("count", 5);

        Notification notification = Notification.builder()
                .userId(UUID.randomUUID())
                .title("Test")
                .category(NotificationCategory.SYSTEM)
                .level(NotificationLevel.INFO)
                .delivered(false)
                .read(false)
                .retryCount(3)
                .translationArgs(translationArgs)
                .build();

        assertThat(notification.getTranslationArgs()).isNotNull();
        assertThat(notification.getTranslationArgs()).containsEntry("userName", "John Doe");
        assertThat(notification.getTranslationArgs()).containsEntry("count", 5);
    }

    @Test
    @DisplayName("Should handle optional fields as null")
    void shouldHandleOptionalFieldsAsNull() {
        Notification notification = Notification.builder()
                .userId(UUID.randomUUID())
                .category(NotificationCategory.ANNOUNCEMENT)
                .level(NotificationLevel.INFO)
                .delivered(false)
                .read(false)
                .retryCount(3)
                .build();

        assertThat(notification.getSenderId()).isNull();
        assertThat(notification.getTitle()).isNull();
        assertThat(notification.getBody()).isNull();
        assertThat(notification.getResourceId()).isNull();
        assertThat(notification.getActionUrl()).isNull();
        assertThat(notification.getSendDate()).isNull();
        assertThat(notification.getTranslationArgs()).isNull();
    }

    @Test
    @DisplayName("Should have correct table name constant")
    void shouldHaveCorrectTableName() {
        assertThat(Notification.TABLE_NAME).isEqualTo("notifications");
    }

    @Test
    @DisplayName("Should create notification with all categories")
    void shouldCreateNotificationWithAllCategories() {
        for (NotificationCategory category : NotificationCategory.values()) {
            Notification notification = Notification.builder()
                    .userId(UUID.randomUUID())
                    .category(category)
                    .level(NotificationLevel.INFO)
                    .delivered(false)
                    .read(false)
                    .retryCount(3)
                    .build();

            assertThat(notification.getCategory()).isEqualTo(category);
        }
    }

    @Test
    @DisplayName("Should create notification with all levels")
    void shouldCreateNotificationWithAllLevels() {
        for (NotificationLevel level : NotificationLevel.values()) {
            Notification notification = Notification.builder()
                    .userId(UUID.randomUUID())
                    .category(NotificationCategory.SYSTEM)
                    .level(level)
                    .delivered(false)
                    .read(false)
                    .retryCount(3)
                    .build();

            assertThat(notification.getLevel()).isEqualTo(level);
        }
    }
}


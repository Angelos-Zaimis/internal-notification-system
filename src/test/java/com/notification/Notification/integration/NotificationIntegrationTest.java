package com.notification.Notification.integration;

import com.notification.Notification.entity.Notification;
import com.notification.Notification.entity.NotificationCategory;
import com.notification.Notification.entity.NotificationLevel;
import com.notification.Notification.repository.NotificationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class NotificationIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private NotificationRepository notificationRepository;

    private String baseUrl;

    @BeforeEach
    void setUp() {
        baseUrl = "http://localhost:" + port;
        notificationRepository.deleteAll();
    }

    

    @Test
    @DisplayName("Should persist notification to database")
    void shouldPersistNotificationToDatabase() {
        UUID userId = UUID.randomUUID();
        
        Notification notification = Notification.builder()
                .userId(userId)
                .title("Integration Test Notification")
                .body("Testing database persistence")
                .category(NotificationCategory.SYSTEM)
                .level(NotificationLevel.INFO)
                .delivered(false)
                .read(false)
                .retryCount(3)
                .build();

        Notification saved = notificationRepository.save(notification);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getCreatedAt()).isNotNull();
        
        Notification retrieved = notificationRepository.findById(saved.getId()).orElseThrow();
        assertThat(retrieved.getTitle()).isEqualTo("Integration Test Notification");
        assertThat(retrieved.getUserId()).isEqualTo(userId);
    }

    
}


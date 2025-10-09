package com.notification.Notification.service;

import com.notification.Notification.dto.NotificationTxDTO;
import com.notification.Notification.entity.Notification;
import com.notification.Notification.entity.NotificationCategory;
import com.notification.Notification.entity.NotificationLevel;
import com.notification.Notification.exception.NotFoundException;
import com.notification.Notification.mapper.NotificationMapper;
import com.notification.Notification.repository.NotificationRepository;
import com.notification.Notification.service.authentication.TokenHandler;
import com.notification.Notification.service.notification.NotificationQueryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationQueryServiceTest {

    @Mock
    private NotificationRepository notificationRepository;

    @Mock
    private NotificationMapper notificationMapper;

    @Mock
    private TokenHandler tokenHandler;

    @InjectMocks
    private NotificationQueryService notificationQueryService;

    private UUID userId;
    private UUID notificationId;
    private Notification notification;
    private NotificationTxDTO notificationDTO;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        notificationId = UUID.randomUUID();

        notification = Notification.builder()
                .id(notificationId)
                .userId(userId)
                .title("Test Notification")
                .body("This is a test notification")
                .category(NotificationCategory.MESSAGE)
                .level(NotificationLevel.INFO)
                .delivered(false)
                .read(false)
                .retryCount(3)
                .createdAt(LocalDateTime.now())
                .build();

        notificationDTO = NotificationTxDTO.builder()
                .id(notificationId)
                .userId(userId)
                .title("Test Notification")
                .body("This is a test notification")
                .category(NotificationCategory.MESSAGE)
                .level(NotificationLevel.INFO)
                .read(false)
                .build();
    }

    @Test
    @DisplayName("Should retrieve paginated notifications for user")
    void shouldRetrieveAllNotifications() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Notification> notificationPage = new PageImpl<>(List.of(notification));

        when(tokenHandler.getSubject()).thenReturn(userId.toString());
        when(notificationRepository.findAllByUserId(eq(userId), any(Pageable.class)))
                .thenReturn(notificationPage);
        when(notificationMapper.toTxDTO(notification)).thenReturn(notificationDTO);

        Page<NotificationTxDTO> result = notificationQueryService.handleGetAllNotifications(0, 10);

        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getTitle()).isEqualTo("Test Notification");

        verify(tokenHandler).getSubject();
        verify(notificationRepository).findAllByUserId(eq(userId), any(Pageable.class));
        verify(notificationMapper).toTxDTO(notification);
    }

    @Test
    @DisplayName("Should mark notification as read")
    void shouldMarkNotificationAsRead() {
        when(notificationRepository.findById(notificationId)).thenReturn(Optional.of(notification));
        when(notificationRepository.save(any(Notification.class))).thenReturn(notification);
        when(notificationMapper.toTxDTO(notification)).thenReturn(notificationDTO);

        NotificationTxDTO result = notificationQueryService.handleMarkNotificationAsRead(notificationId);

        assertThat(result).isNotNull();
        assertThat(notification.isRead()).isTrue();

        verify(notificationRepository).findById(notificationId);
        verify(notificationRepository).save(notification);
        verify(notificationMapper).toTxDTO(notification);
    }

    @Test
    @DisplayName("Should throw exception when notification not found for marking as read")
    void shouldThrowExceptionWhenNotificationNotFoundForRead() {
        when(notificationRepository.findById(notificationId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> notificationQueryService.handleMarkNotificationAsRead(notificationId))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Notification with id " + notificationId + " not found");

        verify(notificationRepository).findById(notificationId);
        verify(notificationRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should delete notification successfully")
    void shouldDeleteNotification() {
        when(notificationRepository.findById(notificationId)).thenReturn(Optional.of(notification));
        doNothing().when(notificationRepository).delete(notification);

        notificationQueryService.handleDeleteNotification(notificationId);

        verify(notificationRepository).findById(notificationId);
        verify(notificationRepository).delete(notification);
    }

    @Test
    @DisplayName("Should throw exception when notification not found for deletion")
    void shouldThrowExceptionWhenNotificationNotFoundForDeletion() {
        when(notificationRepository.findById(notificationId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> notificationQueryService.handleDeleteNotification(notificationId))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Notification with id " + notificationId + " not found");

        verify(notificationRepository).findById(notificationId);
        verify(notificationRepository, never()).delete(any());
    }

    @Test
    @DisplayName("Should return empty page when no notifications exist")
    void shouldReturnEmptyPageWhenNoNotifications() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Notification> emptyPage = Page.empty();

        when(tokenHandler.getSubject()).thenReturn(userId.toString());
        when(notificationRepository.findAllByUserId(eq(userId), any(Pageable.class)))
                .thenReturn(emptyPage);

        Page<NotificationTxDTO> result = notificationQueryService.handleGetAllNotifications(0, 10);

        assertThat(result).isNotNull();
        assertThat(result.getContent()).isEmpty();
        assertThat(result.getTotalElements()).isZero();

        verify(tokenHandler).getSubject();
        verify(notificationRepository).findAllByUserId(eq(userId), any(Pageable.class));
    }
}


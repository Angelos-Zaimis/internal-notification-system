package com.notification.Notification.dto;

import com.notification.Notification.entity.NotificationCategory;
import com.notification.Notification.entity.NotificationLevel;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InternalNotificationDTO {
    
    private UUID userId;
    private UUID senderId;
    private String title;
    private String body;
    private UUID resourceId;
    private String actionUrl;
    private NotificationCategory category;
    private NotificationLevel level;
    private LocalDateTime sendDate;
    private Map<String, Object> translationArgs;
}


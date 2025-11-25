package com.notification.Notification.dto;

import com.notification.Notification.entity.NotificationCategory;
import com.notification.Notification.entity.NotificationLevel;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InternalNotificationDTO {
    
    @NotNull(message = "User ID must not be null")
    private UUID userId;
    
    private UUID senderId;
    
    private String title;
    
    private String body;
    
    private UUID resourceId;
    
    private String actionUrl;
    
    @NotNull(message = "Notification category is required")
    private NotificationCategory category;
    
    @NotNull(message = "Notification level is required")
    private NotificationLevel level;
    
    private LocalDateTime sendDate;
    
    private Map<String, Object> translationArgs;
}


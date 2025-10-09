package com.notification.Notification.dto;

import com.notification.Notification.entity.NotificationCategory;
import com.notification.Notification.entity.NotificationLevel;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class NotificationTxDTO {
    @NotNull(message = "Notification ID must not be null")
    private UUID id;

    @NotNull(message = "User ID must not be null")
    private UUID userId;

    @NotNull(message = "Sender ID must not be null")
    private UUID senderId;

    private String title;

    private String body;

    @NotNull(message = "Notification category is required")
    private NotificationCategory category;

    @NotNull(message = "Notification level is required")
    private NotificationLevel level;

    @NotNull(message = "Delivered flag must be set")
    private Boolean delivered;

    @NotNull(message = "Read flag must be set")
    private Boolean read;

    private LocalDateTime sendDate;

    @NotNull(message = "Creation timestamp must be present")
    private LocalDateTime createdAt;

    @NotNull(message = "Resource ID must not be null")
    private UUID resourceId;

    private String actionUrl;

    private Map<String, Object> translationArgs;
}

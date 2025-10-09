package com.notification.Notification.dto;

import com.notification.Notification.entity.NotificationCategory;
import com.notification.Notification.entity.NotificationLevel;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class NotificationRxDTO {
    @NotNull
    private String id;
    @NotNull
    private UUID userId;
    @NotNull
    private LocalDateTime sendDate;
    @NotNull
    private boolean read;
    @NotNull
    private NotificationLevel level;
    @NotNull
    private NotificationCategory category;
    private Object data;
}

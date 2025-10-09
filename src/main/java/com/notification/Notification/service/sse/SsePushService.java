package com.notification.Notification.service.sse;

import com.notification.Notification.dto.NotificationDeliveryDTO;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.UUID;

public interface SsePushService {
    void sendToUser(UUID userId, NotificationDeliveryDTO notification);
    SseEmitter subscribe(UUID userId);
}

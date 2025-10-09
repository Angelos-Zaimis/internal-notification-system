package com.notification.Notification.service.notification;

import com.notification.Notification.dto.InternalNotificationDTO;

public interface NotificationHandler {

    void processNotification(InternalNotificationDTO notification);
}

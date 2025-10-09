package com.notification.Notification.exception;

import org.springframework.dao.DataAccessException;

public class NotificationAccessException extends DataAccessException {

    public NotificationAccessException(String message, Throwable cause) {
        super(message, cause);
    }
}

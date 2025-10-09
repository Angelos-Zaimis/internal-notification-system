package com.notification.Notification.exception;

import com.notification.Notification.exception.NotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jmx.access.InvalidInvocationException;

import java.util.Map;

public class ExceptionHandlerUtil {

    private static final Logger logger = LoggerFactory.getLogger(ExceptionHandlerUtil.class);
    private static final String RESOURCE_NOT_FOUND = "Resource not found";
    private static final String INVALID_INVOCATION = "Invalid invocation";
    private static final String UNEXPECTED_EXCEPTION = "Unexpected exception";
    private static final String MESSAGE = "message";
    private static final String ERROR = "error";

    private ExceptionHandlerUtil() {}

    public static <T> ResponseEntity<T> handleException(Exception ex) {
        if (ex instanceof NotFoundException) {
            logger.debug(RESOURCE_NOT_FOUND, ex);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body((T) Map.of(ERROR, RESOURCE_NOT_FOUND, MESSAGE, ex.getMessage()));

        } else if (ex instanceof InvalidInvocationException) {
            logger.debug(INVALID_INVOCATION, ex);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body((T) Map.of(ERROR, INVALID_INVOCATION, MESSAGE, ex.getMessage()));
        } else {
            logger.debug(UNEXPECTED_EXCEPTION, ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body((T) Map.of((ERROR), UNEXPECTED_EXCEPTION, MESSAGE, ex.getMessage()));
        }
    }

}

package com.notification.Notification.controller.notification;

import com.notification.Notification.configuration.springdoc.SpringDocTags;
import com.notification.Notification.dto.NotificationTxDTO;
import com.notification.Notification.exception.ExceptionHandlerUtil;
import com.notification.Notification.service.notification.NotificationQueryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.security.RolesAllowed;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Tag(name = SpringDocTags.NOTIFICATION)
@SecurityRequirement(name = "oauth2")
@RestController
@RequestMapping("/notifications")
@RolesAllowed({"STUDENT", "ADMIN", "TEACHER"})
@Validated
public class NotificationController {

    private final NotificationQueryService notificationQueryService;

    @Operation(
            summary = "Fetch user notifications with pagination",
            description = "Returns paginated list of notifications for authenticated user",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Notifications retrieved", content = {
                            @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = NotificationTxDTO.class))
                    }),
                    @ApiResponse(responseCode = "400", description = "Invalid parameters"),
                    @ApiResponse(responseCode = "500", description = "Server error occurred")
            },
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @GetMapping(produces = "application/json")
    public ResponseEntity<Page<NotificationTxDTO>> retrieveAllNotifications(
            @RequestParam(defaultValue = "0") @Parameter(description = "Zero-based page index") 
            @Min(value = 0, message = "Page number must be non-negative") int page,
            @RequestParam(defaultValue = "10") @Parameter(description = "Items per page") 
            @Min(value = 1, message = "Page size must be at least 1") 
            @Max(value = 100, message = "Page size must not exceed 100") int size
    ) {
        try {
            Page<NotificationTxDTO> notifications = notificationQueryService.handleGetAllNotifications(page, size);
            log.info("Retrieved {} notifications on page {}", notifications.getNumberOfElements(), page);
            return ResponseEntity.ok(notifications);
        } catch (Exception ex) {
            return ExceptionHandlerUtil.handleException(ex);
        }
    }

    @Operation(
            summary = "Update notification read status",
            description = "Changes the read status of a notification to true",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Status updated successfully", content = {
                            @Content(mediaType = "application/json", schema = @Schema(implementation = NotificationTxDTO.class))
                    }),
                    @ApiResponse(responseCode = "404", description = "Notification does not exist"),
                    @ApiResponse(responseCode = "500", description = "Server error occurred")
            },
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @PutMapping(value = "/{id}/read", produces = "application/json")
    public ResponseEntity<NotificationTxDTO> markNotificationAsRead(
            @PathVariable("id") @Parameter(description = "Notification UUID") UUID id) {
        try {
            NotificationTxDTO notification = notificationQueryService.handleMarkNotificationAsRead(id);
            return ResponseEntity.ok(notification);
        } catch (Exception ex) {
            return ExceptionHandlerUtil.handleException(ex);
        }
    }

    @Operation(
            summary = "Remove notification",
            description = "Permanently deletes a notification from the system",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Deletion completed"),
                    @ApiResponse(responseCode = "404", description = "Notification does not exist"),
                    @ApiResponse(responseCode = "500", description = "Server error occurred")
            },
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @DeleteMapping(value = "/{id}", produces = "application/json")
    public ResponseEntity<Void> deleteNotification(
            @PathVariable("id") @Parameter(description = "Notification UUID") UUID id) {
        try {
            notificationQueryService.handleDeleteNotification(id);
            log.info("Deleted notification with ID: {}", id);
            return ResponseEntity.noContent().build();
        } catch (Exception ex) {
            return ExceptionHandlerUtil.handleException(ex);
        }
    }
}

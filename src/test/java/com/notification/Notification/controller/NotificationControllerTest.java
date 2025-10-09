package com.notification.Notification.controller;

import com.notification.Notification.controller.notification.NotificationController;
import com.notification.Notification.dto.NotificationTxDTO;
import com.notification.Notification.entity.NotificationCategory;
import com.notification.Notification.entity.NotificationLevel;
import com.notification.Notification.exception.NotFoundException;
import com.notification.Notification.service.notification.NotificationQueryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(NotificationController.class)
@AutoConfigureMockMvc(addFilters = false)
class NotificationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private NotificationQueryService notificationQueryService;

    private UUID notificationId;
    private NotificationTxDTO notificationDTO;

    @BeforeEach
    void setUp() {
        notificationId = UUID.randomUUID();
        
        notificationDTO = NotificationTxDTO.builder()
                .id(notificationId)
                .userId(UUID.randomUUID())
                .title("Assignment Submitted")
                .body("Your assignment has been submitted successfully")
                .category(NotificationCategory.ASSIGNMENT)
                .level(NotificationLevel.INFO)
                .read(false)
                .createdAt(LocalDateTime.now())
                .build();
    }

    @Test
    @WithMockUser(roles = "STUDENT")
    @DisplayName("Should retrieve paginated notifications")
    void shouldRetrievePaginatedNotifications() throws Exception {
        Page<NotificationTxDTO> page = new PageImpl<>(List.of(notificationDTO));
        
        when(notificationQueryService.handleGetAllNotifications(anyInt(), anyInt()))
                .thenReturn(page);

        mockMvc.perform(get("/notifications")
                        .param("page", "0")
                        .param("size", "10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].title").value("Assignment Submitted"))
                .andExpect(jsonPath("$.content[0].category").value("ASSIGNMENT"))
                .andExpect(jsonPath("$.totalElements").value(1));

        verify(notificationQueryService).handleGetAllNotifications(0, 10);
    }

    @Test
    @WithMockUser(roles = "STUDENT")
    @DisplayName("Should mark notification as read")
    void shouldMarkNotificationAsRead() throws Exception {
        notificationDTO.setRead(true);
        
        when(notificationQueryService.handleMarkNotificationAsRead(notificationId))
                .thenReturn(notificationDTO);

        mockMvc.perform(put("/notifications/{id}/read", notificationId)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(notificationId.toString()))
                .andExpect(jsonPath("$.read").value(true));

        verify(notificationQueryService).handleMarkNotificationAsRead(notificationId);
    }

    @Test
    @WithMockUser(roles = "STUDENT")
    @DisplayName("Should return 404 when notification not found")
    void shouldReturn404WhenNotificationNotFound() throws Exception {
        when(notificationQueryService.handleMarkNotificationAsRead(notificationId))
                .thenThrow(new NotFoundException("Notification not found"));

        mockMvc.perform(put("/notifications/{id}/read", notificationId)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(notificationQueryService).handleMarkNotificationAsRead(notificationId);
    }

    @Test
    @WithMockUser(roles = "STUDENT")
    @DisplayName("Should delete notification successfully")
    void shouldDeleteNotification() throws Exception {
        doNothing().when(notificationQueryService).handleDeleteNotification(notificationId);

        mockMvc.perform(delete("/notifications/{id}", notificationId)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(notificationQueryService).handleDeleteNotification(notificationId);
    }

    @Test
    @WithMockUser(roles = "STUDENT")
    @DisplayName("Should use default pagination parameters")
    void shouldUseDefaultPaginationParameters() throws Exception {
        Page<NotificationTxDTO> page = Page.empty();
        
        when(notificationQueryService.handleGetAllNotifications(0, 10))
                .thenReturn(page);

        mockMvc.perform(get("/notifications")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(notificationQueryService).handleGetAllNotifications(0, 10);
    }

    @Test
    @WithMockUser(roles = "TEACHER")
    @DisplayName("Should allow teacher to access notifications")
    void shouldAllowTeacherAccess() throws Exception {
        Page<NotificationTxDTO> page = new PageImpl<>(List.of(notificationDTO));
        
        when(notificationQueryService.handleGetAllNotifications(0, 10))
                .thenReturn(page);

        mockMvc.perform(get("/notifications")
                        .param("page", "0")
                        .param("size", "10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}


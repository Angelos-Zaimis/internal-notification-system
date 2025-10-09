package com.notification.Notification.controller.sse;

import com.notification.Notification.configuration.springdoc.SpringDocTags;
import com.notification.Notification.service.sse.SsePushService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.security.RolesAllowed;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Tag(name = SpringDocTags.NOTIFICATION)
@RestController
@RequestMapping("/sse")
@RolesAllowed({"STUDENT", "ADMIN", "TEACHER"})
public class SseController {

    private final SsePushService ssePushService;

    @GetMapping(value = "/subscribe/{userId}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter subscribe(@PathVariable UUID userId, HttpServletResponse response) {
        response.setHeader("Cache-Control", "no-store");
        response.setHeader("X-Accel-Buffering", "no");
        return ssePushService.subscribe(userId);
    }
}

package com.notification.Notification.service.sse;

import com.notification.Notification.dto.NotificationDeliveryDTO;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class NuSsePushService implements SsePushService {

    private final Map<UUID, SseEmitter> emitters = new ConcurrentHashMap<>();

    public SseEmitter subscribe(UUID userId) {
        log.info("Subscribing user {}", userId);

        SseEmitter emitter = new SseEmitter(0L); // no timeout
        emitters.put(userId, emitter);

        // Remove emitter on completion/error
        emitter.onCompletion(() -> {
            emitters.remove(userId);
            log.info("User {} unsubscribed (completed)", userId);
        });

        emitter.onTimeout(() -> {
            emitters.remove(userId);
            log.warn("SSE timeout for user {}", userId);
        });

        emitter.onError(e -> {
            emitters.remove(userId);
            log.error("SSE error for user {}", userId, e);
        });

        try {
            NotificationDeliveryDTO initial = new NotificationDeliveryDTO();
            initial.setUserId(userId);
            initial.setTitle("Initial message");

            emitter.send(SseEmitter.event()
                    .name("notification")
                    .data(initial)
                    .comment("initial keep-alive"));

            log.info("Initial SSE event sent for user {}", userId);
        } catch (IOException e) {
            log.error("Failed to send initial message to user {}", userId, e);
        }

        return emitter;
    }

    public void sendToUser(UUID userId, NotificationDeliveryDTO notification) {
        SseEmitter emitter = emitters.get(userId);
        if (emitter != null) {
            try {
                emitter.send(SseEmitter.event()
                        .name("notification")
                        .data(notification));
                log.info("Notification sent to user {}", userId);
            } catch (IOException e) {
                log.warn("Emit failed to user {}: {}", userId, e.getMessage());
                emitters.remove(userId);
            }
        } else {
            log.warn("No emitter found for user {}", userId);
        }
    }

    @PostConstruct
    public void startKeepAlive() {
        Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(() -> {
            for (Map.Entry<UUID, SseEmitter> entry : emitters.entrySet()) {
                try {
                    entry.getValue().send(SseEmitter.event()
                            .name("ping")
                            .comment("keep-alive"));
                    log.info("[keepAlive] Ping sent to user {}", entry.getKey());
                } catch (IOException e) {
                    log.warn("Keep-alive failed for user {}: {}", entry.getKey(), e.getMessage());
                    emitters.remove(entry.getKey());
                }
            }
        }, 0, 15, TimeUnit.SECONDS);
    }
}

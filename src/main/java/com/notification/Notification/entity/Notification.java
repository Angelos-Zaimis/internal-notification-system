package com.notification.Notification.entity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Entity
@Table(name = Notification.TABLE_NAME)
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@EntityListeners(AuditingEntityListener.class)
public class Notification {

    public static final String TABLE_NAME = "notifications";

    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "sender_id")
    private UUID senderId;

    @Column(name = "title")
    private String title;

    @Column(name = "body")
    private String body;

    @Column(name = "resource_id")
    private UUID resourceId;

    @Enumerated(EnumType.STRING)
    @Column(name = "category", nullable = false)
    private NotificationCategory category;

    @Enumerated(EnumType.STRING)
    @Column(name = "level", nullable = false)
    private NotificationLevel level;

    @Column(name = "delivered", nullable = false)
    private boolean delivered;

    @Column(name = "read", nullable = false)
    private boolean read;

    @Column(name = "retry_count")
    private int retryCount;

    @Column(name = "action_url")
    private String actionUrl;

    @Column(name = "send_date")
    private LocalDateTime sendDate;

    @Column(name = "created_at")
    @CreationTimestamp
    private LocalDateTime createdAt;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "translation_args")
    private Map<String, Object> translationArgs;

}

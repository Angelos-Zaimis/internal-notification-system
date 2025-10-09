package com.notification.Notification.configuration;

import com.notification.Notification.service.authentication.TokenHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.support.ScopeNotActiveException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.auditing.DateTimeProvider;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Configuration
@Slf4j
@EnableJpaAuditing(auditorAwareRef = "auditorProvider", dateTimeProviderRef = "auditingDateTimeProvider")
public class JpaAuditingConfiguration {

    private final String serviceAccount;
    private final TokenHandler tokenHandler;

    public JpaAuditingConfiguration(@Value("${jpa.fallback-provider-id}") String serviceAccount,
                                    TokenHandler tokenHandler) {
        this.serviceAccount = serviceAccount;
        this.tokenHandler = tokenHandler;
    }

    @Bean(name = "auditingDateTimeProvider")
    public DateTimeProvider dateTimeProvider() {
        return () -> Optional.of(LocalDateTime.now());
    }


    @Bean
    public AuditorAware<UUID> auditorProvider() {
        return () -> {
            String subject = null;
            try {
                subject = tokenHandler.getSubject();
                log.debug("JPA activity for user: {}", subject);
            } catch (ScopeNotActiveException | IllegalStateException e) {
                log.info("Token handler access outside request session.");
            }
            UUID user = UUID.fromString(Optional.ofNullable(subject).orElse(serviceAccount));
            return Optional.of(user);
        };
    }
}

package com.notification.Notification.configuration.springdoc;

import io.swagger.v3.oas.models.OpenAPI;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
@ConditionalOnProperty(value = "springdoc.swagger-ui.enabled", havingValue = "true")
public class SpringDocConfig {

    @Bean
    @ConditionalOnMissingBean(value = SpringDocOauthConfig.class)
    static OpenAPI openApi() {
        return new OpenAPI().tags(SpringDocTags.tags());
    }

}


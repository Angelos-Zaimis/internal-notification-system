package com.notification.Notification.configuration.springdoc;

import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.OAuthFlow;
import io.swagger.v3.oas.annotations.security.OAuthFlows;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
@ConditionalOnProperty(value = {"springdoc.swagger-ui.enabled", "springdoc.token-url"})
@SecurityScheme(name = "oauth2",
        type = SecuritySchemeType.OAUTH2,
        flows = @OAuthFlows(
                password = @OAuthFlow(
                        // client id is recognized automatically form springdoc.swagger-ui.oauth.clientId
                        tokenUrl = "${springdoc.token-url}")
        ))
public class SpringDocOauthConfig {

    @Bean
    static OpenAPI openApi() {
        return new OpenAPI().info(new Info().title("Middleware Notification Management")).tags(SpringDocTags.tags());
    }

}

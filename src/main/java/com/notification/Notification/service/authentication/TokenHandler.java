package com.notification.Notification.service.authentication;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

@Component
public class TokenHandler {
    
    public String getSubject() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof Jwt jwt) {
            return jwt.getSubject();
        }
        return null;
    }
    
    public String getClaim(String claimName) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof Jwt jwt) {
            Object claim = jwt.getClaims().get(claimName);
            return claim != null ? claim.toString() : null;
        }
        return null;
    }
}


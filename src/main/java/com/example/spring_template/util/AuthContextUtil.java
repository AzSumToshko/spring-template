package com.example.spring_template.util;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Optional;

public class AuthContextUtil {

    public static HttpServletRequest getCurrentRequest() {
        var attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        return attrs != null ? attrs.getRequest() : null;
    }

    public static Optional<String> getCurrentUserIdentifier() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getPrincipal())) {
            Object principal = auth.getPrincipal();
            if (principal instanceof UserDetails userDetails) {
                return Optional.ofNullable(userDetails.getUsername()); // Typically email or ID
            } else if (principal instanceof String s) {
                return Optional.of(s);
            }
        }
        return Optional.empty();
    }

    public static Optional<String> getCurrentUserRole() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth != null && auth.isAuthenticated() && auth.getAuthorities() != null && !auth.getAuthorities().isEmpty()) {
            return Optional.of(auth.getAuthorities().iterator().next().getAuthority()); // "ROLE_USER" or "ADMIN"
        }

        return Optional.empty();
    }

    public static String getIpAddress() {
        HttpServletRequest request = getCurrentRequest();
        if (request == null) return null;

        String xForwardedFor = request.getHeader("X-Forwarded-For");
        String remoteAddr = request.getRemoteAddr();

        String ipv4 = null;
        String ipv6 = null;

        if (xForwardedFor != null && !xForwardedFor.isBlank()) {
            String[] ipList = xForwardedFor.split(",");
            for (String ip : ipList) {
                String trimmed = ip.trim();
                if (trimmed.contains(":")) {
                    ipv6 = trimmed;
                } else {
                    ipv4 = trimmed;
                }
            }
        }

        // Fallback to remoteAddr if needed
        if (ipv4 == null && remoteAddr != null && !remoteAddr.contains(":")) ipv4 = remoteAddr;
        if (ipv6 == null && remoteAddr != null && remoteAddr.contains(":")) ipv6 = remoteAddr;

        if (ipv4 != null && ipv6 != null)
            return String.format("IPv4=%s | IPv6=%s", ipv4, ipv6);
        else if (ipv4 != null)
            return String.format("IPv4=%s", ipv4);
        else if (ipv6 != null)
            return String.format("IPv6=%s", ipv6);
        else
            return null;
    }

    public static String getUserAgent() {
        HttpServletRequest request = getCurrentRequest();
        return request != null ? request.getHeader("User-Agent") : null;
    }
}
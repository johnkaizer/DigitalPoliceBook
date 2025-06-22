package com.kca_2sem_project.digitalob.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Arrays;
import java.util.List;

@Component
public class SessionInterceptor implements HandlerInterceptor {

    private static final List<String> PUBLIC_PATHS = Arrays.asList(
            "/",
            "/api/users/login",
            "/api/users/reset-password",
            "/api/users/verify-reset",
            "/licence",
            "/static/**",
            "/api/licenses/**"
    );

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        // Allow public paths without session check
        String requestPath = request.getRequestURI();
        if (PUBLIC_PATHS.stream().anyMatch(path ->
                requestPath.equals(path) || requestPath.matches(path.replace("**", ".*")))) {
            return true;
        }

        // Check if user is logged in
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("loggedInUser") == null) {
            // If it's an API request, return 401 Unauthorized
            if (requestPath.startsWith("/api/")) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return false;
            }

            // For web pages, use a full page redirect with absolute path
            if (request.getHeader("X-Requested-With") == null) {  // Not an AJAX request
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                String baseUrl = request.getScheme() + "://" + request.getServerName() +
                        ":" + request.getServerPort();
                response.sendRedirect(baseUrl + "/");
                return false;
            }
        }
        return true;
    }
}

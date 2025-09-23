package com.team.updevic001.configuration.config.oauth2;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.filter.OncePerRequestFilter;

public class CaptureRedirectParamFilter extends OncePerRequestFilter {
    public static final String ATTR = "REDIRECT_AFTER_LOGIN";

    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain)
            throws ServletException, java.io.IOException {

        // Matches: /{contextPath}/oauth2/authorization/{registrationId}
        String prefix = req.getContextPath() + "/oauth2/authorization/";
        if (req.getRequestURI().startsWith(prefix)) {
            String target = req.getParameter("redirect");
            if (target != null && !target.isBlank()) {
                req.getSession(true).setAttribute(ATTR, target);
            }
        }
        chain.doFilter(req, res);
    }
}
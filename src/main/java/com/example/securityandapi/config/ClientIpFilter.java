package com.example.securityandapi.config;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.logging.log4j.ThreadContext;
import org.springframework.stereotype.Component;

@Component
public class ClientIpFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request,
                         ServletResponse response,
                         FilterChain chain) {
        try {
            HttpServletRequest req = (HttpServletRequest) request;

            // Get real IP (proxy-safe)
            String ip = req.getHeader("X-Forwarded-For");
            if (ip == null || ip.isEmpty()) {
                ip = req.getRemoteAddr();
            }

            // Put IP into Log4j MDC
            ThreadContext.put("clientIp", ip);

            chain.doFilter(request, response);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // VERY IMPORTANT: avoid memory leaks
            ThreadContext.clearAll();
        }
    }
}

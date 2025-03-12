package com.blog.blog_pg.middleware;

import com.blog.blog_pg.utils.ApiUtils;
import com.blog.blog_pg.utils.IBackendRes;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Component
public class AccountAuthFilter extends OncePerRequestFilter {

    @Value("https://back.pg.taphoaictu.id.vn/api/v1")
    private String urlServiceBack;

    @Autowired
    private final ObjectMapper objectMapper;

    public AccountAuthFilter(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        List<String> excludedUrls = Arrays.asList(
                "/api/v1/no-authen",
                "/auth/login",
                "/auth/register"
        );

        String requestURI = request.getRequestURI();

        if (excludedUrls.contains(requestURI)) {

            ObjectMapper objectMapper = new ObjectMapper();
            Account account = objectMapper.convertValue(new Account("1","vminhduc8@gmail.com","**********","user","admin","1234567890","1234567890"), Account.class);
            SecurityContextHolder.getContext().setAuthentication(new AccountAuthenticationToken(account));

            filterChain.doFilter(request, response);
        }else {

            String accessTokenRtr = extractToken(request, "x-at-rtr");
            String refreshTokenRtr = extractToken(request, "x-rf-rtr");

            String accessTokenEpl = extractToken(request, "x-at-epl");
            String refreshTokenEpl = extractToken(request, "x-rf-epl");

            String accessToken = (accessTokenRtr != null) ? accessTokenRtr : accessTokenEpl;
            String refreshToken = (refreshTokenRtr != null) ? refreshTokenRtr : refreshTokenEpl;

            if (accessToken == null || refreshToken == null) {
                writeErrorResponse(response, HttpStatus.UNAUTHORIZED.value(), "Token không hợp lệ 99", -10);
                return;
            }

            String keyAccess = (accessTokenEpl != null) ? "x-at-epl" : "x-at-rtr";
            String keyRefresh = (refreshTokenEpl != null) ? "x-rf-epl" : "x-rf-rtr";

            try {
                IBackendRes<Account> responseData = ApiUtils.sendPostRequest(
                        urlServiceBack + "/restaurants/authen",
                        keyAccess, "Bearer " + accessToken,
                        keyRefresh, "Bearer " + refreshToken,
                        objectMapper
                );


                if (responseData.getStatusCode() == HttpStatus.UNAUTHORIZED.value()) {
                    writeErrorResponse(response, HttpStatus.UNAUTHORIZED.value(), responseData.getMessage(), -10);
                }
                if (responseData.getStatusCode() == HttpStatus.FORBIDDEN.value()) {
                    writeErrorResponse(response, HttpStatus.FORBIDDEN.value(), responseData.getMessage(), -10);
                }
                if (responseData.getStatusCode() != HttpStatus.CREATED.value()) {
                    writeErrorResponse(response, HttpStatus.UNAUTHORIZED.value(), "Token không hợp lệ 98", -10);
                }

                ObjectMapper objectMapper = new ObjectMapper();
                Account account = objectMapper.convertValue(responseData.getData(), Account.class);

                SecurityContextHolder.getContext().setAuthentication(new AccountAuthenticationToken(account));

                filterChain.doFilter(request, response);
            } catch (Exception e) {
                writeErrorResponse(response, HttpStatus.UNAUTHORIZED.value(), "Token không hợp lệ 97", -10);
            }
        }




    }

    private String extractToken(HttpServletRequest request, String headerName) {
        String headerValue = request.getHeader(headerName);
        return (headerValue != null && headerValue.startsWith("Bearer ")) ? headerValue.substring(7) : null;
    }

    private void writeErrorResponse(HttpServletResponse response, int status, String message, int errorCode) throws IOException {
        response.setStatus(status);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        String jsonResponse = objectMapper.writeValueAsString(Map.of(
                "statusCode", status,
                "message", message,
                "code", errorCode
        ));

        response.getWriter().write(jsonResponse);
        response.flushBuffer();
    }

}

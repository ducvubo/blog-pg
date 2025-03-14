package com.blog.blog_pg.utils;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.IndexRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Component
public class LoggingFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(LoggingFilter.class);
    private static final String INDEX_LOG_API_SUCCESS = "log-api-success-blog-pg";
    private static final String INDEX_LOG_API_ERROR = "log-api-error-blog-pg";

    @Autowired
    private ElasticsearchClient elasticsearchClient;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final ThreadLocal<Long> startTime = new ThreadLocal<>();

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        ContentCachingRequestWrapper wrappedRequest = new ContentCachingRequestWrapper(request);
        ContentCachingResponseWrapper wrappedResponse = new ContentCachingResponseWrapper(response);

        String path = wrappedRequest.getRequestURI();
        if (path.startsWith("/api/v1/upload/view-image") || path.equals("/metrics") || path.equals("/api/metrics") || path.equals("/api/v1/metrics")) {
            filterChain.doFilter(wrappedRequest, wrappedResponse);
            return;
        }

        startTime.set(System.currentTimeMillis());
        String idUserGuest = wrappedRequest.getHeader("id_user_guest");
        String idUserGuestNew = "Guest-" + UUID.randomUUID().toString();

        if (idUserGuest == null || "undefined".equals(idUserGuest)) {
            idUserGuest = idUserGuestNew;
            wrappedResponse.setHeader("id_user_guest", idUserGuest);
        } else {
            wrappedResponse.setHeader("id_user_guest", idUserGuest);
        }

        try {
            filterChain.doFilter(wrappedRequest, wrappedResponse);

            long duration = System.currentTimeMillis() - startTime.get();
            logSuccess(wrappedRequest, wrappedResponse, idUserGuest, duration);
        } catch (Exception e) {
            // Nếu exception xảy ra trong filter, ghi log ngay tại đây
            long duration = System.currentTimeMillis() - startTime.get();
            logError(wrappedRequest, wrappedResponse, idUserGuest, duration, e);
            throw e; // Ném lại để GlobalExceptionHandler xử lý
        } finally {
            wrappedResponse.copyBodyToResponse();
            startTime.remove(); // Xóa ThreadLocal sau khi dùng xong
        }
    }

    public void logError(HttpServletRequest request, HttpServletResponse response, String idUserGuest, long duration, Exception ex) {
        ContentCachingRequestWrapper wrappedRequest = new ContentCachingRequestWrapper(request);
        ContentCachingResponseWrapper wrappedResponse = new ContentCachingResponseWrapper(response);

        String path = wrappedRequest.getRequestURI();
        String userAgent = wrappedRequest.getHeader("user-agent");
        String clientIp = wrappedRequest.getRemoteAddr();
        String method = wrappedRequest.getMethod();
        String queryString = wrappedRequest.getQueryString();
        String requestBody = getRequestBody(wrappedRequest);
        String errorMessage = ex.getMessage() != null ? ex.getMessage() : "Unknown error";
        int statusCode = wrappedResponse.getStatus();

        String message = String.format(
                "\n - path: %s\n - statusCode: %d\n - METHOD: %s\n - id_user_guest: %s\n - time: %s\n - duration: %dms",
                path, statusCode, method, idUserGuest, formatDate(new Date()), duration
        );

        logger.error(message + "\n - error: " + errorMessage, ex);
        saveLogApiError(idUserGuest, userAgent, clientIp, new Date(), duration, "", requestBody, errorMessage,
                method, queryString, path, statusCode);
    }

    private void logSuccess(ContentCachingRequestWrapper request, ContentCachingResponseWrapper response,
                            String idUserGuest, long duration) {
        String path = request.getRequestURI();
        String userAgent = request.getHeader("user-agent");
        String clientIp = request.getRemoteAddr();
        String method = request.getMethod();
        String queryString = request.getQueryString();
        String requestBody = getRequestBody(request);
        String responseBody = getResponseBody(response);
        int statusCode = response.getStatus();

        String message = String.format(
                "\n - path: %s\n - statusCode: %d\n - METHOD: %s\n - id_user_guest: %s\n - time: %s\n - duration: %dms",
                path, statusCode, method, idUserGuest, formatDate(new Date()), duration
        );

        logger.info(message);
        saveLogApiSuccess(idUserGuest, userAgent, clientIp, new Date(), duration, "", requestBody, responseBody,
                method, queryString, path, statusCode);
    }

    private String getRequestBody(ContentCachingRequestWrapper request) {
        byte[] content = request.getContentAsByteArray();
        if (content.length > 2000) {
            return "Data too long";
        }
        return content.length > 0 ? new String(content, StandardCharsets.UTF_8) : "No data";
    }

    private String getResponseBody(ContentCachingResponseWrapper response) {
        byte[] content = response.getContentAsByteArray();
        if (content.length > 2000) {
            return "Data too long";
        }
        return content.length > 0 ? new String(content, StandardCharsets.UTF_8) : "No data";
    }

    private String formatDate(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss - dd/MM/yyyy");
        return sdf.format(date);
    }

    private void saveLogApiSuccess(String idUserGuest, String userAgent, String clientIp, Date time, long duration,
                                   String message, String requestBody, String responseBody, String method,
                                   String params, String path, int statusCode) {
        try {
            Map<String, Object> logData = new HashMap<>();
            logData.put("id_user_guest", idUserGuest);
            logData.put("userAgent", userAgent);
            logData.put("clientIp", clientIp);
            logData.put("time", formatDate(time));
            logData.put("duration", duration);
            logData.put("message", message);
            logData.put("bodyRequest", requestBody);
            logData.put("bodyResponse", responseBody);
            logData.put("method", method);
            logData.put("params", params != null ? params : "");
            logData.put("path", path);
            logData.put("statusCode", statusCode);

            IndexRequest<Map<String, Object>> indexRequest = new IndexRequest.Builder<Map<String, Object>>()
                    .index(INDEX_LOG_API_SUCCESS)
                    .id(UUID.randomUUID().toString())
                    .document(logData)
                    .build();

            elasticsearchClient.index(indexRequest);
        } catch (IOException e) {
            logger.error("Failed to save success log to Elasticsearch", e);
        }
    }

    private void saveLogApiError(String idUserGuest, String userAgent, String clientIp, Date time, long duration,
                                 String message, String requestBody, String errorMessage, String method,
                                 String params, String path, int statusCode) {
        try {
            Map<String, Object> logData = new HashMap<>();
            logData.put("id_user_guest", idUserGuest);
            logData.put("userAgent", userAgent);
            logData.put("clientIp", clientIp);
            logData.put("time", formatDate(time));
            logData.put("duration", duration);
            logData.put("message", message);
            logData.put("bodyRequest", requestBody);
            logData.put("bodyResponse", errorMessage);
            logData.put("method", method);
            logData.put("params", params != null ? params : "");
            logData.put("path", path);
            logData.put("statusCode", statusCode);

            IndexRequest<Map<String, Object>> indexRequest = new IndexRequest.Builder<Map<String, Object>>()
                    .index(INDEX_LOG_API_ERROR)
                    .id(UUID.randomUUID().toString())
                    .document(logData)
                    .build();

            elasticsearchClient.index(indexRequest);
        } catch (IOException e) {
            logger.error("Failed to save error log to Elasticsearch", e);
        }
    }

    // Getter để lấy startTime từ GlobalExceptionHandler
    public Long getStartTime() {
        return startTime.get();
    }
}
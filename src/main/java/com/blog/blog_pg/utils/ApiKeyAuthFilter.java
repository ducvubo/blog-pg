package com.blog.blog_pg.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.lettuce.core.api.StatefulRedisConnection;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.env.Environment;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

@Component
public class ApiKeyAuthFilter extends OncePerRequestFilter {

    private static final String SIGN_HEADER = "sign";
    private static final String NONCE_HEADER = "nonce";
    private static final String STIME_HEADER = "stime";
    private static final long EXPIRATION_TIME = 30 * 1000; // 30 giây
    private static final int REDIS_EXPIRATION_SECONDS = 30;
    private static final String REDIS_SIGN_PREFIX = "SIGN_TOKEN:";

    private final Environment environment;
    private final ObjectMapper objectMapper;
    private final RedisUtils redisUtils;

    private static final String KEY_TOKEN = "knjasnjfjnasjnfkjakjfaknjfnkja";
    private static final String VERSION_TOKEN = "v1";

    public ApiKeyAuthFilter(Environment environment, ObjectMapper objectMapper, StatefulRedisConnection<String, String> redisConnection) {
        this.environment = environment;
        this.objectMapper = objectMapper;
        this.redisUtils = RedisUtils.getInstance(redisConnection);
    }

    public static String generateSign(Map<String, String> headers) throws NoSuchAlgorithmException {
        TreeMap<String, String> sortedHeaders = new TreeMap<>(headers);
        StringBuilder headersString = new StringBuilder();

        for (Map.Entry<String, String> entry : sortedHeaders.entrySet()) {
            headersString.append(entry.getKey()).append(entry.getValue());
        }

        String rawString = headersString + KEY_TOKEN + VERSION_TOKEN;
        return md5Hash(rawString);
    }

    private static String md5Hash(String input) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("MD5");
        byte[] digest = md.digest(input.getBytes());
        StringBuilder sb = new StringBuilder();
        for (byte b : digest) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {
            List<String> excludedUrls = Arrays.asList(
                    "/api/v1/no-authen",
                    "/auth/login",
                    "/auth/register",
                    "/api/v1/elasticsearch/create-index-and-add-data"
            );

            String requestURI = request.getRequestURI();

            if (excludedUrls.contains(requestURI)) {
                filterChain.doFilter(request, response);
            }
            String signClient = request.getHeader(SIGN_HEADER);
            String nonce = request.getHeader(NONCE_HEADER);
            String stimeStr = request.getHeader(STIME_HEADER);

            if (signClient == null || nonce == null || stimeStr == null) {
                writeErrorResponse(response, HttpServletResponse.SC_CONFLICT, "Sign token không hợp lệ", 403);
                return;
            }

            String redisKey = REDIS_SIGN_PREFIX + signClient;
            String cachedSign = redisUtils.getCacheIO(redisKey, String.class);
            if (cachedSign != null) {
                writeErrorResponse(response, HttpServletResponse.SC_CONFLICT, "Sign token không hợp lệ", 403);
                return;
            }

            long clientTime = Long.parseLong(stimeStr);
            long currentTime = System.currentTimeMillis();
            if ((currentTime - clientTime) > EXPIRATION_TIME) {
                writeErrorResponse(response, HttpServletResponse.SC_CONFLICT, "Sign token không hợp lệ", 403);
                return;
            }

            Map<String, String> signParams = Map.of("nonce", nonce, "stime", stimeStr);
            String signServer = generateSign(signParams);

            if (!signClient.equals(signServer)) {
                writeErrorResponse(response, HttpServletResponse.SC_CONFLICT, "Sign token không hợp lệ", 403);
                return;
            }

            redisUtils.setCacheIOExpiration(redisKey, signClient, REDIS_EXPIRATION_SECONDS);

            filterChain.doFilter(request, response);

        } catch (Exception e) {
//            writeErrorResponse(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Server error", 500);
        }
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
package com.blog.blog_pg.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.*;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

public class ApiUtils {
    private static final RestTemplate restTemplate = new RestTemplate(new HttpComponentsClientHttpRequestFactory());

    public static <T> IBackendRes<T> sendPostRequest(String url, String keyAccess, String accessToken,
                                                     String keyRefresh, String refreshToken, ObjectMapper objectMapper) {

        HttpHeaders headers = new HttpHeaders();
        headers.set(keyAccess, accessToken);
        headers.set(keyRefresh, refreshToken);

        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);

        try {
            return objectMapper.readValue(response.getBody(), IBackendRes.class);
        } catch (Exception e) {
            return new IBackendRes<>(HttpStatus.UNAUTHORIZED.value(), "Lỗi khi gửi request", null);
        }
    }
}

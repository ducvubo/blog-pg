package com.blog.blog_pg.middleware;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
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
import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import java.security.interfaces.RSAPublicKey;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

@Component
public class AccountAuthFilter extends OncePerRequestFilter {

    @Value("https://back.pg.taphoaictu.id.vn/api/v1")
    private String urlServiceBack;

    @Autowired
    private final ObjectMapper objectMapper;

    public AccountAuthFilter(ObjectMapper objectMapper, ElasticsearchClient elasticsearchClient) {
        this.objectMapper = objectMapper;
        this.elasticsearchClient = elasticsearchClient;
    }

    private static final String EMPLOYEE_INDEX = "employees";
    private static final String RESTAURANT_INDEX = "restaurants";
    private static final String ACCOUNT_INDEX = "accounts";
    private static final String REFRESH_TOKEN_INDEX = "refresh-tokens-account";

    private final ElasticsearchClient elasticsearchClient;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        List<String> excludedUrls = Arrays.asList(
                "/api/v1/no-authen",
                "/auth/login",
                "/auth/register",
                "/api/v1/elasticsearch/create-index-and-add-data",
                "/api/v1/categories/all-category-view",
                "/api/v1/articles/all-article-view",
                "/api/v1/articles/slug",
                "/api/v1/comments",
                "/add/default-n8n"
        );

        String requestURI = request.getRequestURI();

        if (excludedUrls.contains(requestURI)) {

            ObjectMapper objectMapper = new ObjectMapper();
            Account account = objectMapper.convertValue(new Account("67822070f142dddb4e76a252","vminhduc8@gmail.com","**********","user","admin","677aac262fc0d1491a5ca032","1234567890"), Account.class);
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

            try {
                Map<String, String> tokenData = findRefreshToken(refreshToken);
                if (tokenData == null) {
                    writeErrorResponse(response, HttpStatus.UNAUTHORIZED.value(), "Token không hợp lệ", -10);
                    return;
                }



                String accessTokenPublicKey = tokenData.get("rf_public_key_access_token");
                String refreshTokenPublicKey = tokenData.get("rf_public_key_refresh_token");

                RSAPublicKey rsaAccessTokenKey = convertStringToRSAPublicKey(accessTokenPublicKey);
                RSAPublicKey rsaRefreshTokenKey = convertStringToRSAPublicKey(refreshTokenPublicKey);

                String decodedAccess = verifyToken(accessToken, rsaAccessTokenKey);
                String decodedRefresh = verifyToken(refreshToken, rsaRefreshTokenKey);

                if (decodedAccess == null || decodedRefresh == null) {
                    writeErrorResponse(response, HttpStatus.UNAUTHORIZED.value(), "Token không hợp lệ", -10);
                    return;
                }

                // Tìm thông tin account
                Account account = findAccountById(decodedAccess);
                if (account == null) {
                    writeErrorResponse(response, HttpStatus.UNAUTHORIZED.value(), "Token không hợp lệ", -10);
                    return;
                }

                // Kiểm tra loại account
                if ("restaurant".equals(account.getAccountType())) {
                    Map<String, Object> restaurant = findRestaurantById(account.getAccountRestaurantId());
                    if (restaurant == null) {
                        writeErrorResponse(response, HttpStatus.UNAUTHORIZED.value(), "Token không hợp lệ", -10);
                        return;
                    }
                } else if ("employee".equals(account.getAccountType())) {
                    Map<String, Object> employee = findEmployeeById(account.getAccountEmployeeId());
                    if (employee == null) {
                        writeErrorResponse(response, HttpStatus.UNAUTHORIZED.value(), "Token không hợp lệ", -10);
                        return;
                    }
                }
                ObjectMapper objectMapper = new ObjectMapper();
//                Account account = objectMapper.convertValue(responseData.getData(), Account.class);

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

    private Map<String, String> findRefreshToken(String refreshToken) throws Exception {
        SearchRequest searchRequest = new SearchRequest.Builder()
                .index(REFRESH_TOKEN_INDEX)
                .query(q -> q.match(m -> m.field("rf_refresh_token").query(refreshToken)))
                .build();

        SearchResponse<HashMap> response = elasticsearchClient.search(searchRequest, HashMap.class);

        if (response.hits().hits().isEmpty()) {
            return null;
        }

        Map<String, Object> source = response.hits().hits().get(0).source();
        Map<String, String> result = new HashMap<>();
        result.put("rf_public_key_refresh_token", (String) source.get("rf_public_key_refresh_token"));
        result.put("rf_public_key_access_token", (String) source.get("rf_public_key_access_token"));
        return result;
    }

    public static String verifyToken(String token, RSAPublicKey publicKey) {
        try {
            DecodedJWT decodedJWT = JWT.require(Algorithm.RSA256(publicKey, null))
                    .build()
                    .verify(token);

            return decodedJWT.getClaim("_id").asString(); // Lấy giá trị _id
        } catch (JWTVerificationException e) {
            throw new RuntimeException("Token không hợp lệ!", e);
        }
    }

    private Account findAccountById(String id) throws Exception {
        SearchRequest searchRequest = new SearchRequest.Builder()
                .index(ACCOUNT_INDEX)
                .query(q -> q.match(m -> m.field("_id").query(id)))
                .build();

        SearchResponse<Account> response = elasticsearchClient.search(searchRequest, Account.class);

        if (response.hits().hits().isEmpty()) {
            return null;
        }

        return response.hits().hits().get(0).source();
    }

    private Map<String, Object> findRestaurantById(String id) throws Exception {
        SearchRequest searchRequest = new SearchRequest.Builder()
                .index(RESTAURANT_INDEX)
                .query(q -> q.bool(b -> b
                        .must(m -> m.match(m1 -> m1.field("_id").query(id)))
                        .must(m -> m.match(m1 -> m1.field("isDeleted").query(false)))
                        .must(m -> m.match(m1 -> m1.field("restaurant_status").query("inactive")))
                ))
                .build();

        SearchResponse<HashMap> response = elasticsearchClient.search(searchRequest, HashMap.class);

        return response.hits().hits().isEmpty() ? null : response.hits().hits().get(0).source();
    }

    private Map<String, Object> findEmployeeById(String id) throws Exception {
        SearchRequest searchRequest = new SearchRequest.Builder()
                .index(EMPLOYEE_INDEX)
                .query(q -> q.bool(b -> b
                        .must(m -> m.match(m1 -> m1.field("_id").query(id)))
                        .must(m -> m.match(m1 -> m1.field("isDeleted").query(false)))
                        .must(m -> m.match(m1 -> m1.field("epl_status").query("enable")))
                ))
                .build();

        SearchResponse<HashMap> response = elasticsearchClient.search(searchRequest, HashMap.class);

        return response.hits().hits().isEmpty() ? null : response.hits().hits().get(0).source();
    }

    public static RSAPublicKey convertStringToRSAPublicKey(String publicKeyPEM) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
        // Xóa phần header và footer của PEM
        String publicKeyPEMFormatted = publicKeyPEM
                .replace("-----BEGIN PUBLIC KEY-----", "")
                .replace("-----END PUBLIC KEY-----", "")
                .replaceAll("\\s+", ""); // Xóa khoảng trắng

        // Giải mã Base64
        byte[] encoded = Base64.getDecoder().decode(publicKeyPEMFormatted);

        // Chuyển thành RSAPublicKey
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(encoded);
        PublicKey publicKey = keyFactory.generatePublic(keySpec);

        return (RSAPublicKey) publicKey;
    }

    private void saveLogSystem(String action, String className, String function, String message, Exception error) {
        // Implement logging logic here
        Map<String, Object> log = new HashMap<>();
        log.put("action", action);
        log.put("class", className);
        log.put("function", function);
        log.put("message", message);
        log.put("time", new java.util.Date());
        log.put("type", "error");
        // Add to your logging system
    }
}

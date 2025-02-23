package com.blog.blog_pg.middleware;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

public class CustomUserNamePassworDefault extends UsernamePasswordAuthenticationFilter {

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) {
        // Không cần kiểm tra username/password
        // Tạo một đối tượng Authentication với thông tin mặc định
        String username = "defaultUser"; // Hoặc để trống nếu không cần
        String password = ""; // Để trống vì không cần mật khẩu

        // Tạo đối tượng Authentication với quyền mặc định (nếu cần)
        return new CustomAuthenticationToken(username, password, AuthorityUtils.NO_AUTHORITIES);
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
                                            Authentication authResult) throws IOException, ServletException {
        chain.doFilter(request, response);
    }
}
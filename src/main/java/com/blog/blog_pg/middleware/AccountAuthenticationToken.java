package com.blog.blog_pg.middleware;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

public class AccountAuthenticationToken implements Authentication {
    private final Account account;
    private boolean authenticated = true;

    public AccountAuthenticationToken(Account account) {
        this.account = account;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Trả về các quyền của tài khoản
        return null;  // Bạn có thể cần implement thêm logic về quyền (authorities)
    }

    @Override
    public Object getCredentials() {
        return null; // Không cần mật khẩu hoặc thông tin nhạy cảm
    }

    @Override
    public Object getDetails() {
        return null;
    }

    @Override
    public Object getPrincipal() {
        return account;  // Trả về đối tượng tài khoản IAccount
    }

    @Override
    public boolean isAuthenticated() {
        return authenticated;
    }

    @Override
    public void setAuthenticated(boolean authenticated) throws IllegalArgumentException {
        this.authenticated = authenticated;
    }

    @Override
    public String getName() {
        return account.getAccountEmail();  // Trả về email hoặc ID của tài khoản
    }
}

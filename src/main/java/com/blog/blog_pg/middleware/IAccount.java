package com.blog.blog_pg.middleware;

public interface IAccount {
    String getId();
    String getAccountEmail();
    String getAccountPassword();
    String getAccountType(); // 'restaurant' | 'employee'
    String getAccountRole();
    String getAccountRestaurantId();
    String getAccountEmployeeId();
}


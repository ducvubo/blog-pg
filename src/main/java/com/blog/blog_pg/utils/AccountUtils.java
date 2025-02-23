package com.blog.blog_pg.utils;

import com.blog.blog_pg.middleware.Account;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.HashMap;
import java.util.Map;

public class AccountUtils {

    public static String convertAccountToJson(Account account) {
        try {
            // Tạo đối tượng Map để lưu thông tin
            Map<String, Object> accountInfo = new HashMap<>();
            accountInfo.put("email", account.getAccountEmail());
            accountInfo.put("_id", account.getAccountEmployeeId() != null ? account.getAccountEmployeeId() : account.getAccountRestaurantId());

            // Chuyển đổi đối tượng Map thành chuỗi JSON
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.writeValueAsString(accountInfo);
        } catch (Exception e) {
            e.printStackTrace();
            return "{}";
        }
    }
}

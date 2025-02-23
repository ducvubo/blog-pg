package com.blog.blog_pg.middleware;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
@JsonIgnoreProperties(ignoreUnknown = true)
public class Account implements IAccount {

    private String id;
    private String accountEmail;
    private String accountPassword;
    private String accountType; // 'restaurant' | 'employee'
    private String accountRole;
    private String accountRestaurantId;
    private String accountEmployeeId;

    // Constructor với @JsonCreator
    @JsonCreator
    public Account(
            @JsonProperty("id") String id,
            @JsonProperty("account_email") String accountEmail,
            @JsonProperty("account_password") String accountPassword,
            @JsonProperty("account_type") String accountType,
            @JsonProperty("account_role") String accountRole,
            @JsonProperty("account_restaurant_id") String accountRestaurantId,
            @JsonProperty("account_employee_id") String accountEmployeeId) {
        this.id = id;
        this.accountEmail = accountEmail;
        this.accountPassword = accountPassword;
        this.accountType = accountType;
        this.accountRole = accountRole;
        this.accountRestaurantId = accountRestaurantId;
        this.accountEmployeeId = accountEmployeeId;
    }

    // Getter và Setter
    @Override
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String getAccountEmail() {
        return accountEmail;
    }

    public void setAccountEmail(String accountEmail) {
        this.accountEmail = accountEmail;
    }

    @Override
    public String getAccountPassword() {
        return accountPassword;
    }

    public void setAccountPassword(String accountPassword) {
        this.accountPassword = accountPassword;
    }

    @Override
    public String getAccountType() {
        return accountType;
    }

    public void setAccountType(String accountType) {
        this.accountType = accountType;
    }

    @Override
    public String getAccountRole() {
        return accountRole;
    }

    public void setAccountRole(String accountRole) {
        this.accountRole = accountRole;
    }

    @Override
    public String getAccountRestaurantId() {
        return accountRestaurantId;
    }

    public void setAccountRestaurantId(String accountRestaurantId) {
        this.accountRestaurantId = accountRestaurantId;
    }

    @Override
    public String getAccountEmployeeId() {
        return accountEmployeeId;
    }

    public void setAccountEmployeeId(String accountEmployeeId) {
        this.accountEmployeeId = accountEmployeeId;
    }
}

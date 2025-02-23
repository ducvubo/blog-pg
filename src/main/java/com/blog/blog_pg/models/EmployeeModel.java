package com.blog.blog_pg.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class EmployeeModel {
    @JsonProperty("_id")
    private String _id;

    @JsonProperty("epl_restaurant_id")
    private String epl_restaurant_id;

    @JsonProperty("epl_name")
    private String epl_name;

    @JsonProperty("epl_email")
    private String epl_email;

    @JsonProperty("epl_phone")
    private String epl_phone;

    @JsonProperty("epl_gender")
    private String epl_gender;

    @JsonProperty("epl_address")
    private String epl_address;

    @JsonProperty("isDeleted")
    private Boolean isDeleted;

    @JsonProperty("epl_status")
    private String epl_status;

    // Getters & Setters (bắt buộc để Jackson hoạt động)
    public String get_id() { return _id; }
    public void set_id(String _id) { this._id = _id; }

    public String getEpl_restaurant_id() { return epl_restaurant_id; }
    public void setEpl_restaurant_id(String epl_restaurant_id) { this.epl_restaurant_id = epl_restaurant_id; }

    public String getEpl_name() { return epl_name; }
    public void setEpl_name(String epl_name) { this.epl_name = epl_name; }

    public String getEpl_email() { return epl_email; }
    public void setEpl_email(String epl_email) { this.epl_email = epl_email; }

    public String getEpl_phone() { return epl_phone; }
    public void setEpl_phone(String epl_phone) { this.epl_phone = epl_phone; }

    public String getEpl_gender() { return epl_gender; }
    public void setEpl_gender(String epl_gender) { this.epl_gender = epl_gender; }

    public String getEpl_address() { return epl_address; }
    public void setEpl_address(String epl_address) { this.epl_address = epl_address; }

    public Boolean getIsDeleted() { return isDeleted; }
    public void setIsDeleted(Boolean isDeleted) { this.isDeleted = isDeleted; }

    public String getEpl_status() { return epl_status; }
    public void setEpl_status(String epl_status) { this.epl_status = epl_status; }

    @Override
    public String toString() {
        return "Employee{" +
                "_id='" + _id + '\'' +
                ", epl_restaurant_id='" + epl_restaurant_id + '\'' +
                ", epl_name='" + epl_name + '\'' +
                ", epl_email='" + epl_email + '\'' +
                ", epl_phone='" + epl_phone + '\'' +
                ", epl_gender='" + epl_gender + '\'' +
                ", epl_address='" + epl_address + '\'' +
                ", isDeleted='" + isDeleted + '\'' +
                ", epl_status='" + epl_status + '\'' +
                '}';
    }
}

package com.blog.blog_pg.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Builder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateNotification {

    @JsonProperty("noti_acc_id")
    private String notiAccId;

    @JsonProperty("noti_title")
    private String notiTitle;

    @JsonProperty("noti_content")
    private String notiContent;

    @JsonProperty("noti_type")
    private String notiType;

    @JsonProperty("noti_metadata")
    private String notiMetadata;

    @JsonProperty("sendObject")
    private String sendObject;
}
package com.blog.blog_pg.entities;

import com.blog.blog_pg.enums.ArticleStatus;
import com.blog.blog_pg.enums.ArticleType;
import com.blog.blog_pg.listener.ArticleEntityListener;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "article")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(ArticleEntityListener.class)
public class ArticleEntity {

    @Id
    @GeneratedValue(generator = "UUID")
    @Column(updatable = false, nullable = false,columnDefinition = "RAW(16)")
    private UUID atlId;

    @Column(name = "atlResId")
    private String atlResId;

    @Column(name = "catId")
    private String catId;

    @Column(name = "atlTitle")
    private String atlTitle;

    @Column(name = "atlDescription")
    private String atlDescription;

    @Column(name="atlSlug")
    private String atlSlug;

    @Column(name = "atlImage",columnDefinition  = "CLOB")
    private String atlImage;

    @Enumerated(EnumType.STRING)
    @Column(name = "atlType")
    private ArticleType atlType;

    @Column(name = "atlContent", columnDefinition  = "CLOB")
    private String atlContent;

    @Enumerated(EnumType.STRING)
    @Column(name = "atlStatus")
    private ArticleStatus atlStatus;

    @Column(name = "atlPublishedTime")
    private Date atlPublishedTime;

    @Column(name = "atlPublishedSchedule")
    private Date atlPublishedSchedule;

    @Column(name = "atlView")
    private int atlView;

    @ElementCollection
    @Fetch(FetchMode.JOIN)
    @CollectionTable(name = "ArticleRelated", joinColumns = @JoinColumn(name = "alt_id"))
    @Column(name = "atlrelated_id")
    private List<String> listArticleRelated;

    @ElementCollection
    @Fetch(FetchMode.JOIN)
    @CollectionTable(name = "ArticleNote", joinColumns = @JoinColumn(name = "alt_id"))
    @Column(name = "atlNote")
    private List<String> listArticleNote;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "createdAt")
    private Date createdAt;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "updatedAt")
    private Date updatedAt;

    @Column(name = "deletedAt")
    private Date deletedAt;

    @Column(name = "createdBy")
    private String createdBy;

    @Column(name = "updatedBy")
    private String updatedBy;

    @Column(name = "deletedBy")
    private String deletedBy;

    @Column(name = "isDeleted",columnDefinition = "int default 0")
    private int isDeleted;

    @PrePersist
    protected void onCreate() {
        Date now = new Date();
        this.createdAt = now;
        this.updatedAt = now;
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = new Date();
    }
}

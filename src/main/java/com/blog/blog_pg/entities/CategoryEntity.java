package com.blog.blog_pg.entities;

import com.blog.blog_pg.enums.EnumStatus;
import com.blog.blog_pg.listener.CategoryEntityListener;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.UUID;

@Entity
@Table(name = "category")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(CategoryEntityListener.class)
public class CategoryEntity {
    @Id
    @GeneratedValue(generator = "UUID")
    @Column(updatable = false, nullable = false,columnDefinition = "RAW(16)")
    private UUID catId;

    @Column(name = "catResId")
    private String catResId;

    @Column(name = "catName")
    private String catName;

    @Column(name = "catSlug")
    private String catSlug;

    @Column(name = "catDescription")
    private String catDescription;

    @Column(name = "catOrder")
    private int catOrder;

    @Column(name = "catStatus")
    private EnumStatus catStatus;

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

    @Column(name = "isDeleted")
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

package com.blog.blog_pg.entities;

import com.blog.blog_pg.enums.EnumStatus;
import jakarta.persistence.*;
import lombok.*;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "label")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LabelEntity {

    @Id
    @GeneratedValue(generator = "UUID")
    @Column(updatable = false, nullable = false,columnDefinition = "RAW(16)")
    private UUID lb_id;

    @Column(name = "lb_res_id")
    private String lb_res_id;

    @Column(name = "lb_name")
    private String lb_name;

    @Column(name = "lb_description")
    private String lb_description;

    @Column(name = "lb_color")
    private String lb_color;

    @Enumerated(EnumType.STRING)
    @Column(name = "lb_status")
    private EnumStatus lb_status;

//    @OneToMany(mappedBy = "label", fetch = FetchType.LAZY)
//    @JsonIgnore
//    private List<WorkScheduleEntity> workSchedules;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at")
    private Date createdAt;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "updated_at")
    private Date updatedAt;

    @Column(name = "deleted_at")
    private Date deletedAt;

    @Column(name = "created_by")
    private String createdBy;

    @Column(name = "updated_by")
    private String updatedBy;

    @Column(name = "deleted_by")
    private String deletedBy;

    @Column(name = "is_deleted")
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
package com.msxwtech.prototype.common.audite;

import com.msxwtech.prototype.common.entity.BaseEntity;
import org.hibernate.envers.Audited;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Column;
import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;

@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
@Audited
public abstract class AuditableEntity extends BaseEntity {

    @Column(name = "created_date", nullable = false, updatable = false)
    @CreatedDate
    private Long createdDatetime;

    @Column(name = "modified_date")
    @LastModifiedDate
    private Long modifiedDatetime;

    public Long getCreatedDatetime() {
        return createdDatetime;
    }

    public Long getModifiedDatetime() {
        return modifiedDatetime;
    }
}

package yu.shen.pocboot.common.audite;

import org.hibernate.envers.Audited;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import yu.shen.pocboot.common.entity.BaseEntity;

import javax.persistence.Column;
import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;

@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
@Audited
public abstract class AuditableEntity extends BaseEntity  {

    @Column(name = "created_date", nullable = false, updatable = false)
    @CreatedDate
    private Long createdDatetime;

    @Column(name = "modified_date")
    @LastModifiedDate
    private Long modifedDatetime;

    public Long getCreatedDatetime() {
        return createdDatetime;
    }

    public Long getModifedDatetime() {
        return modifedDatetime;
    }
}

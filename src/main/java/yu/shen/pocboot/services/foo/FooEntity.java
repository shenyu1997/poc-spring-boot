package yu.shen.pocboot.services.foo;

import org.hibernate.annotations.Where;
import org.hibernate.envers.Audited;
import yu.shen.pocboot.common.audite.AuditableEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "foo")
@Audited
@Where(clause="delete_token='NA'")
public class FooEntity extends AuditableEntity {

    private String description;

    @Column(name = "ct")
    private Integer count;

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}

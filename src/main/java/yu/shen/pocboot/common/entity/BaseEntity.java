package yu.shen.pocboot.common.entity;

import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.UUID;

/**
 * Created by sheyu on 7/17/2018.
 */
@MappedSuperclass
@Audited
public abstract class BaseEntity {
    private static final String DEFAULT_DELETE_TOKEN = "NA";
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @Column(nullable = false, updatable = false)
    private String name;

    @Version
    private long version;

    @Column(name = "delete_token")
    private String delete_token = DEFAULT_DELETE_TOKEN;

    public boolean isDeleted() {
        return DEFAULT_DELETE_TOKEN == delete_token;
    }

    public void setDeleted(boolean deleted) {
        delete_token = deleted? UUID.randomUUID().toString() : DEFAULT_DELETE_TOKEN;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long  getVersion() {
        return version;
    }

    @Override
    public String toString() {
        return "Entity (" + this.getClass().getCanonicalName() + ") {" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", version=" + version +
                ", isDeleted=" + isDeleted() +
                '}';
    }
}
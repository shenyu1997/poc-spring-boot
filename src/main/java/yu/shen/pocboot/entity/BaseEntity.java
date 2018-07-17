package yu.shen.pocboot.entity;

import javax.persistence.*;

/**
 * Created by sheyu on 7/17/2018.
 */
@MappedSuperclass
public abstract class BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;
    private String name;

    @Version
    private long version;

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
                '}';
    }
}

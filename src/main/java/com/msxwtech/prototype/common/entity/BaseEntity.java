package com.msxwtech.prototype.common.entity;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.envers.Audited;
import org.springframework.data.domain.ExampleMatcher;

import javax.persistence.*;
import java.util.Objects;
import java.util.UUID;

/**
 * Created by sheyu on 7/17/2018.
 */
@MappedSuperclass
@Audited
public abstract class BaseEntity {
    public static ExampleMatcher buildDefaultMatch() {
        return ExampleMatcher.matching()
                .withIgnorePaths("version")
                .withIgnorePaths("delete_token")
                .withStringMatcher(ExampleMatcher.StringMatcher.STARTING)   // Match string containing pattern
                .withIgnoreCase();
    }

    private static final String DEFAULT_DELETE_TOKEN = "NA";
    @Id
    @GenericGenerator(name = "long_by_uuid", strategy = "com.msxwtech.prototype.common.entity.LongIdentifierGenerator")
    @GeneratedValue(generator = "long_by_uuid")
    private Long id;

    @Column(nullable = false, updatable = false, unique = true)
    private String name;

    @Version
    private long version;

    @Column(name = "delete_token")
    private String delete_token = DEFAULT_DELETE_TOKEN;

    public boolean isDeleted() {
        return !Objects.equals(DEFAULT_DELETE_TOKEN, delete_token);
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

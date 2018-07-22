package yu.shen.pocboot.common.rsqlsupport;

import org.hibernate.annotations.Where;
import org.springframework.context.annotation.Profile;
import yu.shen.pocboot.common.audite.AuditableEntity;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Profile("test")
@Entity
@Where(clause="delete_token='NA'")
public class RSQLFooEntity extends AuditableEntity {
    public enum Status {ACTIVE, INACTIVE}

    private String description;
    private int count;
    private Integer countInteger;
    private Long countLong;
    private Double size;
    private Float sizeFloat;
    private LocalDate birthday;
    private LocalDateTime when;
    private Boolean isLock;

    @Enumerated(EnumType.STRING)
    private Status status;

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public Integer getCountInteger() {
        return countInteger;
    }

    public void setCountInteger(Integer countInteger) {
        this.countInteger = countInteger;
    }

    public Long getCountLong() {
        return countLong;
    }

    public void setCountLong(Long countLong) {
        this.countLong = countLong;
    }

    public Double getSize() {
        return size;
    }

    public void setSize(Double size) {
        this.size = size;
    }

    public Float getSizeFloat() {
        return sizeFloat;
    }

    public void setSizeFloat(Float sizeFloat) {
        this.sizeFloat = sizeFloat;
    }

    public LocalDate getBirthday() {
        return birthday;
    }

    public void setBirthday(LocalDate birthday) {
        this.birthday = birthday;
    }

    public LocalDateTime getWhen() {
        return when;
    }

    public void setWhen(LocalDateTime when) {
        this.when = when;
    }

    public Boolean getLock() {
        return isLock;
    }

    public void setLock(Boolean lock) {
        isLock = lock;
    }

    @Override
    public String toString() {
        return "RSQLFooEntity{" +
                "description='" + description + '\'' +
                ", count=" + count +
                ", countInteger=" + countInteger +
                ", countLong=" + countLong +
                ", size=" + size +
                ", sizeFloat=" + sizeFloat +
                ", birthday=" + birthday +
                ", when=" + when +
                ", isLock=" + isLock +
                '}';
    }
}

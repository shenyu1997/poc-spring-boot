package yu.shen.pocboot.services.foo;

import yu.shen.pocboot.common.audite.AuditableDTO;

public class FooDTO extends AuditableDTO {

    private String description;

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

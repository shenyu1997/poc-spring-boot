package yu.shen.pocboot.services.foo;

import yu.shen.pocboot.common.audite.AuditableDTO;

public class FooDTO extends AuditableDTO {

    private String description;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}

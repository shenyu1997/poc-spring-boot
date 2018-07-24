package yu.shen.pocboot.services.bar;

import yu.shen.pocboot.common.audite.AuditableDTO;

public class BarDTO extends AuditableDTO {
    private String description;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}

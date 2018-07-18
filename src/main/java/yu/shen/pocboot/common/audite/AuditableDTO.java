package yu.shen.pocboot.common.audite;

import yu.shen.pocboot.common.entity.BaseDTO;

public abstract class AuditableDTO extends BaseDTO {
    private Long createdDatetime;
    private Long modifiedDatetime;

    public Long getCreatedDatetime() {
        return createdDatetime;
    }

    public void setCreatedDatetime(Long createdDatetime) {
        this.createdDatetime = createdDatetime;
    }

    public Long getModifiedDatetime() {
        return modifiedDatetime;
    }

    public void setModifiedDatetime(Long modifiedDatetime) {
        this.modifiedDatetime = modifiedDatetime;
    }
}

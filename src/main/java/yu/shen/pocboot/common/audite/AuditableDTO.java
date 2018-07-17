package yu.shen.pocboot.common.audite;

import yu.shen.pocboot.common.entity.BaseDTO;

public abstract class AuditableDTO extends BaseDTO {
    private Long createdDatetime;
    private Long modifedDatetime;

    public Long getCreatedDatetime() {
        return createdDatetime;
    }

    public void setCreatedDatetime(Long createdDatetime) {
        this.createdDatetime = createdDatetime;
    }

    public Long getModifedDatetime() {
        return modifedDatetime;
    }

    public void setModifedDatetime(Long modifedDatetime) {
        this.modifedDatetime = modifedDatetime;
    }
}

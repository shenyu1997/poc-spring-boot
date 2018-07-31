package com.msxwtech.prototype.services.bar;

import com.msxwtech.prototype.common.audite.AuditableDTO;

public class BarDTO extends AuditableDTO {
    private String description;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}

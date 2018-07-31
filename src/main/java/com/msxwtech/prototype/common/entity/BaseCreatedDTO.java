package com.msxwtech.prototype.common.entity;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

public class BaseCreatedDTO {
    @NotNull
    @Pattern(regexp = ".*\\D+.*")
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

package com.msxwtech.prototype.services.foo;

import com.msxwtech.prototype.common.entity.BaseCreatedDTO;

public class FooCreatedDTO extends BaseCreatedDTO {


    private String description;

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }

    private String count;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}

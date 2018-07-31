package com.msxwtech.prototype.common.pagination;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.data.domain.PageRequest;

import java.util.Optional;

public class PageableDTO extends PageRequest {
    public static final int DEFAULT_PAGE_SIZE = 15;

    public static PageableDTO empty() {
        return new PageableDTO(1,DEFAULT_PAGE_SIZE, null);
    }

    @JsonCreator
    @JsonIgnoreProperties(ignoreUnknown = true)
    public PageableDTO(@JsonProperty("pageNumber") Integer page,
                       @JsonProperty("pageSize") Integer size,
                       @JsonProperty("sort") SortDTO sort) {
        super(page == null? 1:page, size == null? DEFAULT_PAGE_SIZE : size, sort);
    }
}

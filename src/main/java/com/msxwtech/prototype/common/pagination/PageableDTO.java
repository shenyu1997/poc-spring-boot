package com.msxwtech.prototype.common.pagination;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.data.domain.PageRequest;

import java.util.Optional;

public class PageableDTO extends PageRequest {
    public static final int DEFAULT_PAGE_SIZE = 15;

    public static PageableDTO empty() {
        return new PageableDTO(Optional.empty(),Optional.empty(), null);
    }

    @JsonCreator
    @JsonIgnoreProperties(ignoreUnknown = true)
    public PageableDTO(@JsonProperty("pageNumber") Optional<Integer> page,
                       @JsonProperty("pageSize") Optional<Integer> size,
                       @JsonProperty("sort") SortDTO sort) {
        super(page.orElse(0), size.orElse(DEFAULT_PAGE_SIZE), sort);
    }
}

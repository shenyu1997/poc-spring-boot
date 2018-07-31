package com.msxwtech.prototype.common.pagination;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.data.domain.Sort;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class SortDTO extends Sort {
    public static final String NA = "NA";

    @JsonCreator
    @JsonIgnoreProperties(ignoreUnknown = true)
    public SortDTO(@JsonProperty("direction") Direction direction,
                   @JsonProperty("properties") List<String> properties) {
        super(direction, properties == null? Arrays.asList(NA) : properties);
    }
}

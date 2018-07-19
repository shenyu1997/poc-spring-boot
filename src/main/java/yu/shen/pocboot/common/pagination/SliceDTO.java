package yu.shen.pocboot.common.pagination;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.data.domain.SliceImpl;

import java.util.List;

public class SliceDTO<T> extends SliceImpl<T> {

    @JsonCreator
    @JsonIgnoreProperties(ignoreUnknown = true)
    public SliceDTO(@JsonProperty("content") List<T> content,
                    @JsonProperty("pageable") PageableDTO pageable,
                    @JsonProperty("hasNext") boolean hasNext) {
        super(content, pageable, hasNext);
    }
}

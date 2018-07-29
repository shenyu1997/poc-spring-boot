package yu.shen.pocboot.common.pagination;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;

import java.util.Collections;
import java.util.List;

public class SliceDTO<T> extends SliceImpl<T> {

    public static <T> Slice<T> empty() {
        return new SliceDTO<>(Collections.emptyList(),PageableDTO.empty(),false);
    }

    @JsonCreator
    @JsonIgnoreProperties(ignoreUnknown = true)
    public SliceDTO(@JsonProperty("content") List<T> content,
                    @JsonProperty("pageable") PageableDTO pageable,
                    @JsonProperty("hasNext") boolean hasNext) {
        super(content, pageable, hasNext);
    }
}

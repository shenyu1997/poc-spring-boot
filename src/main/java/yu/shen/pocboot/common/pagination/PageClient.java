package yu.shen.pocboot.common.pagination;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.springframework.data.domain.Page;

@JsonDeserialize(as = PageClientImpl.class)
public interface PageClient<T> extends Page<T> {
}

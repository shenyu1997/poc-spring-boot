package com.msxwtech.prototype.common.audite;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface AuditedQueryExecutor<T> {
    List<T> loadHistory(Class<T> tClass, Long entityId, Optional<LocalDateTime> from, Optional<LocalDateTime> to);
}

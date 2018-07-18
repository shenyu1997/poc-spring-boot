package yu.shen.pocboot.common.audite;

import org.hibernate.envers.AuditReader;
import org.hibernate.envers.AuditReaderFactory;
import org.hibernate.envers.query.AuditEntity;
import org.hibernate.envers.query.AuditQuery;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.EntityManager;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class AuditedQueryExecutorImpl<T> implements AuditedQueryExecutor<T> {
    private static final String MODIFIED_DATETIME = "modifiedDatetime";

    @Autowired
    private EntityManager entityManager;


    @Override
    public List<T> loadHistory(Class<T> tClass, Long entityId, Optional<LocalDateTime> from, Optional<LocalDateTime> to) {

        AuditReader auditReader = AuditReaderFactory.get(entityManager);
        AuditQuery query = auditReader.createQuery().forRevisionsOfEntity(tClass, true, false);

        from.ifPresent(f -> {
            query.add(AuditEntity.property(MODIFIED_DATETIME).ge(Timestamp.valueOf(f).getTime()));
        });

        to.ifPresent(t -> {
            query.add(AuditEntity.property(MODIFIED_DATETIME).le(Timestamp.valueOf(t).getTime()));
        });

        query.add(AuditEntity.property("id").eq(entityId));
        query.addOrder(AuditEntity.property(MODIFIED_DATETIME).asc());
        return query.getResultList();
    }

}

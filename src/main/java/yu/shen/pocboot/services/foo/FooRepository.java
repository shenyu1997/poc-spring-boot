package yu.shen.pocboot.services.foo;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import yu.shen.pocboot.common.audite.AuditedQueryExecutor;

import java.util.Optional;

@Repository
public interface FooRepository extends CrudRepository<FooEntity, Long>, AuditedQueryExecutor<FooEntity> {
    Slice<FooEntity> findAll(Pageable pageable);

    Optional<FooEntity> findByName(String name);

    Slice<FooEntity> findByNameStartsWith(String name, Pageable pageable);
}

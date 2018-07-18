package yu.shen.pocboot.services.foo;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import yu.shen.pocboot.common.audite.AuditedQueryExecutor;

import java.util.List;

@Repository
public interface FooRepository extends CrudRepository<FooEntity, Long>, AuditedQueryExecutor<FooEntity> {
    List<FooEntity> findAll();
}

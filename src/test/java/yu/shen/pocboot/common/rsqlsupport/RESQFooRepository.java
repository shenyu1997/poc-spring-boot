package yu.shen.pocboot.common.rsqlsupport;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RESQFooRepository extends CrudRepository<RSQLFooEntity, Long>, JpaSpecificationExecutor<RSQLFooEntity> {
    List<RSQLFooEntity> findAll();
}

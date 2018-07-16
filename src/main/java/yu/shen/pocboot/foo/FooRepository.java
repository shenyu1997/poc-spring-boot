package yu.shen.pocboot.foo;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FooRepository extends CrudRepository<Foo, Long> {
    List<Foo> findAll();
}

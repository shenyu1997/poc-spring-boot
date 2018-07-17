package yu.shen.pocboot.services.foo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import yu.shen.pocboot.common.exceptions.EntityNotFoundException;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class FooService {

    @Autowired
    private FooRepository fooRepository;


    public List<Foo> findAll() {
        return  fooRepository.findAll();
    }

    public Foo create(Foo foo) {
        return fooRepository.save(foo);
    }

    public Optional<Foo> findById(Long id) {
        return fooRepository.findById(id);
    }

    public Foo getById(Long id) {
        return findById(id).orElseThrow(() -> new EntityNotFoundException(Foo.class, id));
    }

    public void update(Foo foo) {
        fooRepository.save(foo);
    }

    public void deleteById(Long id) {
        fooRepository.deleteById(id);
    }
}

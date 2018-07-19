package yu.shen.pocboot.services.foo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import yu.shen.pocboot.common.exceptions.EntityNotFoundException;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class FooService {

    @Autowired
    private FooRepository fooRepository;


    public Slice<FooEntity> findAll(Pageable pageable) {
        return  fooRepository.findAll(pageable);
    }

    public FooEntity create(FooEntity fooEntity) {
        return fooRepository.save(fooEntity);
    }

    public Optional<FooEntity> findById(Long id) {
        return fooRepository.findById(id);
    }

    public FooEntity getById(Long id) {
        return findById(id).orElseThrow(() -> new EntityNotFoundException(FooEntity.class, id));
    }

    public void update(FooEntity fooEntity) {
        fooRepository.save(fooEntity);
    }

    public void deleteById(Long id, Boolean isHard) {
        if (isHard) {
            fooRepository.deleteById(id);
        } else {
            FooEntity foo = this.getById(id);
            foo.setDeleted(true);
            fooRepository.save(foo);
        }
    }

    public List<FooEntity> findHistoryById(Long id, Optional<LocalDateTime> from, Optional<LocalDateTime> to) {
        return fooRepository.loadHistory(FooEntity.class, id, from, to);
    }
}

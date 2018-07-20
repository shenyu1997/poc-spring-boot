package yu.shen.pocboot.services.foo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import com.google.common.primitives.Longs;
import yu.shen.pocboot.common.exceptions.EntityNotFoundException;
import yu.shen.pocboot.common.pagination.SliceDTO;

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

    public Optional<FooEntity> findByIdOrName(String idOrName) {
        Long id = Longs.tryParse(idOrName);
        if(id == null) {
            return fooRepository.findByName(idOrName);
        } else {
            return fooRepository.findById(id);
        }
    }

    public FooEntity getByIdOrName(String idOrName) {
        return findByIdOrName(idOrName).orElseThrow(() -> new EntityNotFoundException(FooEntity.class, idOrName));
    }

    public void update(FooEntity fooEntity) {
        fooRepository.save(fooEntity);
    }

    public void deleteByIdOrName(String idOrName, Boolean isHard) {
        FooEntity foo = this.getByIdOrName(idOrName);
        if (isHard) {
            fooRepository.delete(foo);
        } else {
            foo.setDeleted(true);
            fooRepository.save(foo);
        }
    }

    public List<FooEntity> findHistoryById(Long id, Optional<LocalDateTime> from, Optional<LocalDateTime> to) {
        return fooRepository.loadHistory(FooEntity.class, id, from, to);
    }

    public Slice<FooEntity> findByNameStartsWith(String name, Pageable pageable) {
        return fooRepository.findByNameStartsWith(name, pageable);
    }
}

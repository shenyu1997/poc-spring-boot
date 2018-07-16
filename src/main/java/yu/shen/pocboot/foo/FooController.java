package yu.shen.pocboot.foo;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.util.UriComponents;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/foo")
public class FooController {

    @Autowired
    private FooService fooService;

    @Autowired
    private ModelMapper modelMapper;

    @GetMapping("/ping")
    public String ping() {
        return "hello";
    }

    @GetMapping
    public List<FooListedDTO> findAll() {
        return fooService.findAll()
                .stream()
                .map(foo -> modelMapper.map(foo, FooListedDTO.class))
                .collect(Collectors.toList());
    }

    @PostMapping
    public ResponseEntity<Void> create(@RequestBody FooCreatedDTO fooDTO,
                                       HttpServletRequest request,
                                       ServletResponse response, ServletUriComponentsBuilder uriComponentsBuilder) {

        Long id = fooService.create(modelMapper.map(fooDTO, Foo.class)).getId();
        return ResponseEntity.created(uriComponentsBuilder
                .fromRequest(request)
                .path("/{id}")
                .build()
                .expand(String.valueOf(id))
                .toUri())
                .build();
    }

    @GetMapping("/{id}")
    public FooDetailDTO findById(@PathVariable("id") Long id) {
        Foo result = fooService.findById(id).orElseThrow(() -> new RuntimeException("entity not found"));;
        return modelMapper.map(result, FooDetailDTO.class);
    }

    @PutMapping("/{id}")
    public void update(@PathVariable("id") Long id,
                       @RequestBody FooUpdateddDTO fooDTO) {
        fooService.findById(id).ifPresentOrElse(foo -> {
            modelMapper.map(fooDTO, foo);
            fooService.update(foo);
        }, ()-> {
            throw new RuntimeException("entity not found");
        });
    }
}

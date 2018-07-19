package yu.shen.pocboot.services.foo;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
public class FooController {

    public static final String URI_ENDPOINT = "/foo";
    public static final String URI_SINGLE_RESOURCE_ENDPOINT = URI_ENDPOINT + "/{id}";
    public static final String URI_HISTORY = URI_SINGLE_RESOURCE_ENDPOINT + "/history";

    @Autowired
    private FooService fooService;

    @Autowired
    private ModelMapper modelMapper;

    @GetMapping(URI_ENDPOINT + "/ping")
    public String ping() {
        return "ok";
    }

    @GetMapping(URI_ENDPOINT)
    public Page<FooListedDTO> findAll(@PageableDefault Pageable pageable) {
        return fooService.findAll(pageable).map(fooEntity -> modelMapper.map(fooEntity, FooListedDTO.class));

    }

    @PostMapping(URI_ENDPOINT)
    public ResponseEntity<Void> create(@RequestBody FooCreatedDTO fooDTO,
                                       HttpServletRequest request,
                                       ServletResponse response, ServletUriComponentsBuilder uriComponentsBuilder) {
        Long id = fooService.create(modelMapper.map(fooDTO, FooEntity.class)).getId();
        return ResponseEntity.created(uriComponentsBuilder
                .fromRequest(request)
                .path("/{id}")
                .build()
                .expand(String.valueOf(id))
                .toUri())
                .build();
    }

    @GetMapping(URI_SINGLE_RESOURCE_ENDPOINT)
    public FooDTO getById(@PathVariable("id") Long id) {
        return modelMapper.map(fooService.getById(id), FooDTO.class);
    }

    @PutMapping(URI_SINGLE_RESOURCE_ENDPOINT)
    public void update(@PathVariable("id") Long id,
                       @RequestBody FooUpdatedDTO fooDTO) {
            FooEntity fooEntity = fooService.getById(id);
            modelMapper.map(fooDTO, fooEntity);
            fooService.update(fooEntity);

    }

    @DeleteMapping(URI_SINGLE_RESOURCE_ENDPOINT)
    public void deleteById(@PathVariable("id") Long id, @RequestParam("is_hard") Optional<Boolean> isHard) {
        fooService.deleteById(id, isHard.orElse(false));
    }


    @GetMapping(URI_HISTORY)
    public List<FooDTO> loadHistory(@PathVariable("id") Long id,
                                    @RequestParam("from") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Optional<LocalDateTime> from,
                                    @RequestParam("from") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Optional<LocalDateTime> to) {
        return fooService.findHistoryById(id, from, to).stream()
                .map(foo -> modelMapper.map(foo, FooDTO.class))
                .collect(Collectors.toList());
    }
}

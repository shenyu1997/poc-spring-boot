package yu.shen.pocboot.services.foo;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
public class FooController {

    public static final String URI_ENDPOINT = "/foo";
    public static final String URI_SINGLE_RESOURCE_ENDPOINT = URI_ENDPOINT + "/{idOrName}";
    public static final String URI_HISTORY = URI_SINGLE_RESOURCE_ENDPOINT + "/history";

    public static final String URI_SEARCH_FIND_BY_NAME_STARTSWITH = URI_ENDPOINT + "/search/nameStartsWith";

    @Autowired
    private FooService fooService;

    @Autowired
    private ModelMapper modelMapper;

    @GetMapping(URI_ENDPOINT + "/ping")
    public String ping() {
        return "ok";
    }

    @GetMapping(URI_ENDPOINT)
    public Slice<FooListedDTO> findAll(@PageableDefault Pageable pageable) {
        return fooService.findAll(pageable).map(fooEntity -> modelMapper.map(fooEntity, FooListedDTO.class));

    }

    @PostMapping(URI_ENDPOINT)
    public ResponseEntity<Void> create(@Valid  @RequestBody FooCreatedDTO fooDTO,
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
    public FooDTO getByIdOrName(@PathVariable("idOrName") String idOrName) {
        return modelMapper.map(fooService.getByIdOrName(idOrName), FooDTO.class);
    }

    @PutMapping(URI_SINGLE_RESOURCE_ENDPOINT)
    public void update(@PathVariable("idOrName") String idOrName,
                       @RequestBody FooUpdatedDTO fooDTO) {
            FooEntity fooEntity = fooService.getByIdOrName(idOrName);
            modelMapper.map(fooDTO, fooEntity);
            fooService.update(fooEntity);

    }

    @DeleteMapping(URI_SINGLE_RESOURCE_ENDPOINT)
    public void deleteById(@PathVariable("idOrName") String idOrName, @RequestParam("is_hard") Optional<Boolean> isHard) {
        fooService.deleteByIdOrName(idOrName, isHard.orElse(false));
    }

    @GetMapping(URI_HISTORY)
    public List<FooDTO> loadHistory(@PathVariable("id") Long id,
                                    @RequestParam("from") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Optional<LocalDateTime> from,
                                    @RequestParam("from") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Optional<LocalDateTime> to) {
        return fooService.findHistoryById(id, from, to).stream()
                .map(foo -> modelMapper.map(foo, FooDTO.class))
                .collect(Collectors.toList());
    }

    @GetMapping(URI_SEARCH_FIND_BY_NAME_STARTSWITH)
    public Slice<FooListedDTO> findByNameStartsWith(@RequestParam("name") String name, Pageable pageable) {
        return fooService.findByNameStartsWith(name, pageable).map(foo -> modelMapper.map(foo, FooListedDTO.class));
    }
}

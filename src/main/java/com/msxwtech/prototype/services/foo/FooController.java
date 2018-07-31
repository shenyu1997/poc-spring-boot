package com.msxwtech.prototype.services.foo;

import brave.Span;
import cz.jirutka.rsql.parser.RSQLParser;
import cz.jirutka.rsql.parser.ast.Node;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import com.msxwtech.prototype.common.entity.BaseEntity;
import com.msxwtech.prototype.common.rsqlsupport.GenericRSQLVisitor;

import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
public class FooController {

    private Logger logger = LoggerFactory.getLogger("com.msxwtech.prototype.services.foo");

    public static final String URI_ENDPOINT = "/foo";
    public static final String URI_SINGLE_RESOURCE_ENDPOINT = URI_ENDPOINT + "/{idOrName}";
    public static final String URI_HISTORY = URI_SINGLE_RESOURCE_ENDPOINT + "/history";

    public static final String URI_SEARCH_FIND_BY_NAME_STARTSWITH = URI_ENDPOINT + "/search/nameStartsWith";

    @Autowired
    private FooService fooService;

    @Autowired
    private ModelMapper modelMapper;
    private Span span;

    @GetMapping(URI_ENDPOINT + "/ping")
    public String ping() {
        return "ok";
    }

    @GetMapping(URI_ENDPOINT)
    public Slice<FooListedDTO> findAll(@RequestParam(value = "name", required = false) String name,
                                       @RequestParam(value = "count", required = false) Integer count,
                                       @RequestParam(value = "description", required = false) String description,
                                        @RequestParam(value = "filter") Optional<String> filter,
                                        @PageableDefault Pageable pageable) {
        logger.info("<><><><><><><><><> foo find all");

        fooService.printSomething();
        if(filter.isPresent()) {
            Node root = new RSQLParser().parse(filter.get());
            Specification<FooEntity> accept = root.accept(new GenericRSQLVisitor<>());

            return fooService.findAll(accept, pageable).map(fooEntity -> modelMapper.map(fooEntity, FooListedDTO.class));
        } else {
            FooEntity prob = new FooEntity();
            prob.setName(name);
            prob.setCount(count);
            prob.setDescription(description);

            return fooService.findAll(Example.of(prob, BaseEntity.buildDefaultMatch()), pageable).map(fooEntity -> modelMapper.map(fooEntity, FooListedDTO.class));
        }
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

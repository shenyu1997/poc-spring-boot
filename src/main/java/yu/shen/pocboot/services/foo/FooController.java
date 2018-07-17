package yu.shen.pocboot.services.foo;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.stream.Collectors;

import static yu.shen.pocboot.services.foo.FooController.URI_RESOURCES_ENDPOINT;

@RestController
@RequestMapping(URI_RESOURCES_ENDPOINT)
public class FooController {

    public static final String URI_RESOURCES_ENDPOINT = "/foo";
    public static final String URI_RESOURCE_ENDPOINT = "/{id}";


    @Autowired
    private FooService fooService;

    @Autowired
    private ModelMapper modelMapper;

    @GetMapping("/ping")
    public String ping() {
        return "ok";
    }

    @GetMapping
    public List<FooListedDTO> findAll() {
        return fooService.findAll()
                .stream()
                .map(fooEntity -> modelMapper.map(fooEntity, FooListedDTO.class))
                .collect(Collectors.toList());
    }

    @PostMapping
    public ResponseEntity<Void> create(@RequestBody FooCreatedDTO fooDTO,
                                       HttpServletRequest request,
                                       ServletResponse response, ServletUriComponentsBuilder uriComponentsBuilder) {
        Long id = fooService.create(modelMapper.map(fooDTO, FooEntity.class)).getId();
        return ResponseEntity.created(uriComponentsBuilder
                .fromRequest(request)
                .path(URI_RESOURCE_ENDPOINT)
                .build()
                .expand(String.valueOf(id))
                .toUri())
                .build();
    }

    @GetMapping(URI_RESOURCE_ENDPOINT)
    public FooDTO getById(@PathVariable("id") Long id) {
        return modelMapper.map(fooService.getById(id), FooDTO.class);
    }

    @PutMapping(URI_RESOURCE_ENDPOINT)
    public void update(@PathVariable("id") Long id,
                       @RequestBody FooUpdatedDTO fooDTO) {
            FooEntity fooEntity = fooService.getById(id);
            modelMapper.map(fooDTO, fooEntity);
            fooService.update(fooEntity);

    }

    @DeleteMapping(URI_RESOURCE_ENDPOINT)
    public void deleteById(@PathVariable("id") Long id) {
        fooService.deleteById(id);
    }
}

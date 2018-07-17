package yu.shen.pocboot.services.foo;

import com.fasterxml.jackson.core.type.TypeReference;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import yu.shen.pocboot.IntegrationTest;
import yu.shen.pocboot.common.exceptions.EntityNotFoundException;
import yu.shen.pocboot.common.exceptions.ExceptionDTO;

import javax.transaction.Transactional;
import java.util.List;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Created by sheyu on 7/17/2018.
 */
@Transactional
public class FooEntityControllerTest extends IntegrationTest {

    final String URI_SINGLE_FOO = FooController.URI_RESOURCES_ENDPOINT + FooController.URI_RESOURCE_ENDPOINT;

    @Autowired
    private FooRepository fooRepository;

    private Long fooId;

    @Before
    public void before() {
        FooEntity fooEntity = new FooEntity();
        fooEntity.setName("test");
        fooEntity.setDescription("test desc");
        fooId = fooRepository.save(fooEntity).getId();
    }

    @After
    public void after() {
        fooRepository.deleteAll();
    }

    @Test
    public void findAll() throws Exception {
        String body = mvc.perform(get(FooController.URI_RESOURCES_ENDPOINT))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        List<FooListedDTO> result = objectMapper.readValue(body,new TypeReference<List<FooListedDTO>>(){});
        assertThat(result, hasSize(1));
        assertThat(result.get(0).getId(), equalTo(fooId));
    }

    @Test
    public void getById() throws Exception {
        String body = mvc.perform(get(URI_SINGLE_FOO, fooId))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        FooDTO result = objectMapper.readValue(body, FooDTO.class);
        assertThat(result.getId(), equalTo(fooId));
        assertThat(result.getName(), equalTo("test"));
        assertThat(result.getDescription(), equalTo("test desc"));
        assertThat(result.getCreatedDatetime(), notNullValue());
        assertThat(result.getModifedDatetime(), notNullValue());
    }

    @Test
    public void getByIdNotExist() throws Exception {
        String body = mvc.perform(get(URI_SINGLE_FOO, 1234L))
                .andExpect(status().isNotFound())
                .andReturn().getResponse().getContentAsString();
        ExceptionDTO exceptionDTO = objectMapper.readValue(body, ExceptionDTO.class);
        assertThat(exceptionDTO.getCorrelationId(), notNullValue());
        assertThat(exceptionDTO.getTimestamp(), greaterThan(0L));
        assertThat(exceptionDTO.getCode(), equalTo("entity_not_found"));
    }

    @Test
    public void create() throws Exception {
        FooCreatedDTO fooCreatedDTO = new FooCreatedDTO();
        fooCreatedDTO.setName("new foo");
        fooCreatedDTO.setDescription("new foo description");
        String urlOfNewFoo = mvc.perform(post(FooController.URI_RESOURCES_ENDPOINT).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(fooCreatedDTO)))
                .andExpect(status().isCreated())
                .andExpect(header().exists("location"))
                .andReturn().getResponse().getHeader("location");
        String body = mvc.perform(get(urlOfNewFoo))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        FooDTO returned = objectMapper.readValue(body, FooDTO.class);
        assertThat(returned.getId(), notNullValue());
        assertThat(returned.getName(), equalTo(fooCreatedDTO.getName()));
        assertThat(returned.getDescription(), equalTo(fooCreatedDTO.getDescription()));
        assertThat(returned.getVersion(), equalTo(0L));
    }

    @Test
    @Ignore
    public void createDuplicatedName() throws Exception {
        FooCreatedDTO fooCreatedDTO = new FooCreatedDTO();
        fooCreatedDTO.setName("test");

        mvc.perform(post(FooController.URI_RESOURCES_ENDPOINT).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(fooCreatedDTO)))
                .andExpect(status().isConflict());

    }

    @Test
    public void update() throws Exception {
        FooUpdatedDTO fooUpdatedDTO = new FooUpdatedDTO();
        fooUpdatedDTO.setDescription("update description");

        mvc.perform(put(URI_SINGLE_FOO, fooId).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(fooUpdatedDTO)))
                .andExpect(status().isOk());

        entityManager.flush(); //need flush to update version

        String body = mvc.perform(get(URI_SINGLE_FOO, fooId))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        FooDTO fooDTO = objectMapper.readValue(body, FooDTO.class);
        assertThat(fooDTO.getDescription(), equalTo(fooUpdatedDTO.getDescription()));
        assertThat(fooDTO.getVersion(), equalTo(1L));
    }

    @Test
    public void updateNoExist() throws Exception {
        FooUpdatedDTO fooUpdatedDTO = new FooUpdatedDTO();
        String body = mvc.perform(put(URI_SINGLE_FOO, 1234L).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(fooUpdatedDTO)))
                .andExpect(status().isNotFound())
                .andReturn().getResponse().getContentAsString();
        ExceptionDTO exceptionDTO = objectMapper.readValue(body, ExceptionDTO.class);
        assertThat(exceptionDTO.getCode(),equalTo(EntityNotFoundException.CODE));
    }

    @Test
    public void deleteFoo() throws Exception {
        mvc.perform(delete(URI_SINGLE_FOO, fooId))
            .andExpect(status().isOk());

        mvc.perform(get(FooController.URI_RESOURCES_ENDPOINT))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(equalTo(0)));
    }

    @Test
    public void deleteNoExist() throws Exception {
        String body = mvc.perform(delete(URI_SINGLE_FOO, 1234L))
                .andExpect(status().isNotFound())
                .andReturn().getResponse().getContentAsString();
        ExceptionDTO exceptionDTO = objectMapper.readValue(body, ExceptionDTO.class);
        assertThat(exceptionDTO.getCode(),equalTo(EntityNotFoundException.CODE));
    }
}

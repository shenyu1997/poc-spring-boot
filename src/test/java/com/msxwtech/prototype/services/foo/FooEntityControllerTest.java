package com.msxwtech.prototype.services.foo;

import com.fasterxml.jackson.core.type.TypeReference;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Slice;
import org.springframework.http.MediaType;
import com.msxwtech.prototype.IntegrationTest;
import com.msxwtech.prototype.common.exceptions.EntityNotFoundException;
import com.msxwtech.prototype.common.exceptions.ExceptionDTO;
import com.msxwtech.prototype.common.pagination.SliceDTO;

import java.util.List;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Created by sheyu on 7/17/2018.
 */
public class FooEntityControllerTest extends IntegrationTest {

    public static final String FOO_NAME = "test";
    @Autowired
    private FooRepository fooRepository;

    private Long fooId;

    @Before
    public void before() {
        FooEntity fooEntity = new FooEntity();
        fooEntity.setName(FOO_NAME);
        fooEntity.setCount(100);
        fooEntity.setDescription("test desc");
        fooId = fooRepository.save(fooEntity).getId();
        entityManager.flush();
    }

    @After
    public void after() {
        fooRepository.deleteAll();
    }

    @Test
    public void findAll() throws Exception {
        String body = mvc.perform(get(FooController.URI_ENDPOINT)
                    .param("size","100"))
               .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        SliceDTO<FooListedDTO> result = objectMapper.readValue(body,new TypeReference<SliceDTO<FooListedDTO>>(){});
        assertThat(result.hasNext(), equalTo(false));
        assertThat(result.getNumberOfElements(), equalTo(1));
        assertThat(result.getContent(), hasSize(1));
        assertThat(result.getContent().get(0).getId(), equalTo(fooId));
    }

    @Test
    public void findAllByFilter() throws Exception {
        final String filter = "(count==1,description==test*);name!=" + FOO_NAME;
        String body = mvc.perform(get(FooController.URI_ENDPOINT).param("filter", filter))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        Slice<FooListedDTO> retured = objectMapper.readValue(body, new TypeReference<SliceDTO<FooListedDTO>>(){});
        assertThat(retured.getContent(), hasSize(0));
    }

    @Test
    public void findAllWithExample() throws Exception {
        FooUpdatedDTO fooUpdatedDTO = new FooUpdatedDTO();
        fooUpdatedDTO.setDescription("new desc");
        fooUpdatedDTO.setCount(100);
        mvc.perform(put(FooController.URI_SINGLE_RESOURCE_ENDPOINT,fooId).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsBytes(fooUpdatedDTO)))
                .andExpect(status().isOk());


        String body = mvc.perform(get(FooController.URI_ENDPOINT)
                .param("name","te")
                .param("count", "100"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        SliceDTO<FooListedDTO> result = objectMapper.readValue(body,new TypeReference<SliceDTO<FooListedDTO>>(){});
        assertThat(result.hasNext(), equalTo(false));
        assertThat(result.getNumberOfElements(), equalTo(1));
        assertThat(result.getContent(), hasSize(1));
        assertThat(result.getContent().get(0).getId(), equalTo(fooId));
    }
    @Test
    public void getById() throws Exception {
        String body = mvc.perform(get(FooController.URI_SINGLE_RESOURCE_ENDPOINT, fooId))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        FooDTO result = objectMapper.readValue(body, FooDTO.class);
        assertThat(result.getId(), equalTo(fooId));
        assertThat(result.getName(), equalTo( FOO_NAME));
        assertThat(result.getDescription(), equalTo("test desc"));
        assertThat(result.getCreatedDatetime(), notNullValue());
        assertThat(result.getModifiedDatetime(), notNullValue());
    }

    @Test
    public void getByName() throws Exception {
        String body = mvc.perform(get(FooController.URI_SINGLE_RESOURCE_ENDPOINT, FOO_NAME))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        FooDTO result = objectMapper.readValue(body, FooDTO.class);
        assertThat(result.getId(), equalTo(fooId));
        assertThat(result.getName(), equalTo(FOO_NAME));
        assertThat(result.getDescription(), equalTo("test desc"));
        assertThat(result.getCreatedDatetime(), notNullValue());
        assertThat(result.getModifiedDatetime(), notNullValue());
    }

    @Test
    public void getByIdNotExist() throws Exception {
        String body = mvc.perform(get(FooController.URI_SINGLE_RESOURCE_ENDPOINT, 1234L))
                .andExpect(status().isNotFound())
                .andReturn().getResponse().getContentAsString();
        ExceptionDTO exceptionDTO = objectMapper.readValue(body, ExceptionDTO.class);
        assertThat(exceptionDTO.getCorrelationId(), notNullValue());
        assertThat(exceptionDTO.getTimestamp(), greaterThan(0L));
        assertThat(exceptionDTO.getCode(), equalTo("entity_not_found"));
    }

    @Test
    public void createWithErrorName() throws Exception {
        FooCreatedDTO fooCreatedDTO = new FooCreatedDTO();
        fooCreatedDTO.setName("12345");
        fooCreatedDTO.setDescription("new foo description");
        mvc.perform(post(FooController.URI_ENDPOINT).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(fooCreatedDTO)))
                .andExpect(status().isBadRequest());

    }

    @Test
    public void create() throws Exception {
        FooCreatedDTO fooCreatedDTO = new FooCreatedDTO();
        fooCreatedDTO.setName("new Test");
        fooCreatedDTO.setDescription("new foo description");
        String urlOfNewFoo = mvc.perform(post(FooController.URI_ENDPOINT).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(fooCreatedDTO)))
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
    @Ignore("constraints check will be executed when db operated, but in test env the action will deffer")
    public void createDuplicatedName() throws Exception {
        FooCreatedDTO fooCreatedDTO = new FooCreatedDTO();
        fooCreatedDTO.setName(FOO_NAME);

        mvc.perform(post(FooController.URI_ENDPOINT).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(fooCreatedDTO)))
                .andExpect(status().isConflict());

    }

    @Test
    public void update() throws Exception {
        FooUpdatedDTO fooUpdatedDTO = new FooUpdatedDTO();
        fooUpdatedDTO.setDescription("update description");

        mvc.perform(put(FooController.URI_SINGLE_RESOURCE_ENDPOINT, fooId).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(fooUpdatedDTO)))
                .andExpect(status().isOk());

        entityManager.flush(); //need flush to update version

        String body = mvc.perform(get(FooController.URI_SINGLE_RESOURCE_ENDPOINT, fooId))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        FooDTO fooDTO = objectMapper.readValue(body, FooDTO.class);
        assertThat(fooDTO.getDescription(), equalTo(fooUpdatedDTO.getDescription()));
        assertThat(fooDTO.getVersion(), equalTo(1L));
    }

    @Test
    public void updateNoExist() throws Exception {
        FooUpdatedDTO fooUpdatedDTO = new FooUpdatedDTO();
        String body = mvc.perform(put(FooController.URI_SINGLE_RESOURCE_ENDPOINT, 1234L).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(fooUpdatedDTO)))
                .andExpect(status().isNotFound())
                .andReturn().getResponse().getContentAsString();
        ExceptionDTO exceptionDTO = objectMapper.readValue(body, ExceptionDTO.class);
        assertThat(exceptionDTO.getCode(),equalTo(EntityNotFoundException.CODE));
    }

    @Test
    public void deleteFoo() throws Exception {
        mvc.perform(delete(FooController.URI_SINGLE_RESOURCE_ENDPOINT, fooId))
            .andExpect(status().isOk());

        mvc.perform(get(FooController.URI_ENDPOINT))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(equalTo(0)));
    }

    @Test
    public void deleteNoExist() throws Exception {
        String body = mvc.perform(delete(FooController.URI_SINGLE_RESOURCE_ENDPOINT, 1234L))
                .andExpect(status().isNotFound())
                .andReturn().getResponse().getContentAsString();
        ExceptionDTO exceptionDTO = objectMapper.readValue(body, ExceptionDTO.class);
        assertThat(exceptionDTO.getCode(),equalTo(EntityNotFoundException.CODE));
    }

    @Test
    @Ignore("can not run because audit feature will not be run in integration test env")
    public void loadHistory() throws Exception {
        String body = mvc.perform(get(FooController.URI_HISTORY, fooId))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        List<FooDTO> history = objectMapper.readValue(body, new TypeReference<List<FooDTO>>(){});
        assertThat(history.size(), equalTo(1));

        FooUpdatedDTO fooUpdatedDTO = new FooUpdatedDTO();
        fooUpdatedDTO.setDescription("new history update");

        mvc.perform(put(FooController.URI_SINGLE_RESOURCE_ENDPOINT,fooId).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(fooUpdatedDTO)))
                .andExpect(status().isOk());

        entityManager.flush();

        body = mvc.perform(get(FooController.URI_HISTORY, fooId))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        history = objectMapper.readValue(body, new TypeReference<List<FooDTO>>(){});
        assertThat(history.size(), equalTo(2));
    }

    @Test
    public void findByNameStartsWith() throws Exception {
        FooCreatedDTO fooCreatedDTO = new FooCreatedDTO();
        fooCreatedDTO.setName("test123");
        mvc.perform(post(FooController.URI_ENDPOINT).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsBytes(fooCreatedDTO)))
                .andExpect(status().isCreated());

        String body = mvc.perform(get(FooController.URI_SEARCH_FIND_BY_NAME_STARTSWITH).param("name",FOO_NAME))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        SliceDTO<FooListedDTO> page = objectMapper.readValue(body, new TypeReference<SliceDTO<FooListedDTO>>() {});

        assertThat(page.getContent().size(), equalTo(2));

    }
}

package com.msxwtech.prototype.common.rsqlsupport;

import cz.jirutka.rsql.parser.RSQLParser;
import cz.jirutka.rsql.parser.ast.Node;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import com.msxwtech.prototype.IntegrationTest;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.isIn;

public class GenericRSQLVisitorTest extends IntegrationTest {

    private RSQLFooEntity tom;
    private RSQLFooEntity jerry;
    private RSQLFooEntity deleted;

    @Autowired
    private RESQFooRepository fooRepository;

    @Before
    public void before() {
        RSQLFooEntity tomFoo = new RSQLFooEntity();
        tomFoo.setName("tom");
        tomFoo.setBirthday(LocalDate.of(2000,10,17));
        tomFoo.setCount(100);
        tomFoo.setCountInteger(101);
        tomFoo.setCountLong(102L);
        tomFoo.setDescription("this is tom test data");
        tomFoo.setLock(true);
        tomFoo.setSize(103D);
        tomFoo.setSizeFloat(104F);
        tomFoo.setStatus(RSQLFooEntity.Status.ACTIVE);
        tomFoo.setWhen(LocalDateTime.of(2000,10,17,17,50,32));
        tom = fooRepository.save(tomFoo);

        RSQLFooEntity jerryFoo = new RSQLFooEntity();
        jerryFoo.setName("jerry");
        jerryFoo.setBirthday(LocalDate.of(2005,10,17));
        jerryFoo.setCount(1100);
        jerryFoo.setCountInteger(1101);
        jerryFoo.setCountLong(1102L);
        jerryFoo.setDescription("this is jerry test data");
        jerryFoo.setLock(false);
        jerryFoo.setSize(1103D);
        jerryFoo.setSizeFloat(1104F);
        jerryFoo.setStatus(RSQLFooEntity.Status.INACTIVE);
        jerryFoo.setWhen(LocalDateTime.of(2005,10,17,17,50,32));
        jerry = fooRepository.save(jerryFoo);

        RSQLFooEntity deletedFoo = new RSQLFooEntity();
        deletedFoo.setName("deletedFoo");
        deletedFoo.setDeleted(true);
        deleted = fooRepository.save(deletedFoo);
        entityManager.flush();
    }

    @Test
    public void findAll() {
        List<RSQLFooEntity> all = fooRepository.findAll();
        assertThat(all, hasSize(2));
    }

    @Test
    public void findByStringEqual() {
        Node root = new RSQLParser().parse("name==tom");
        Specification<RSQLFooEntity> accept = root.accept(new GenericRSQLVisitor<RSQLFooEntity>());
        List<RSQLFooEntity> all = fooRepository.findAll(accept);

        assertThat(all, hasSize(1));
        assertThat(tom, isIn(all));
    }

    @Test
    public void findByLike() {
        Node root = new RSQLParser().parse("description==this*");
        Specification<RSQLFooEntity> accept = root.accept(new GenericRSQLVisitor<RSQLFooEntity>());
        List<RSQLFooEntity> all = fooRepository.findAll(accept);

        assertThat(all, hasSize(2));
        assertThat(tom, isIn(all));
        assertThat(jerry, isIn(all));
    }


    @Test
    public void findByEqualInt() {
        Node root = new RSQLParser().parse("count==100");
        Specification<RSQLFooEntity> accept = root.accept(new GenericRSQLVisitor<RSQLFooEntity>());
        List<RSQLFooEntity> all = fooRepository.findAll(accept);

        assertThat(all, hasSize(1));
        assertThat(tom, isIn(all));
    }

    @Test
    public void findByGrantThanInt() {
        Node root = new RSQLParser().parse("count>100");
        Specification<RSQLFooEntity> accept = root.accept(new GenericRSQLVisitor<RSQLFooEntity>());
        List<RSQLFooEntity> all = fooRepository.findAll(accept);

        assertThat(all, hasSize(1));
        assertThat(jerry, isIn(all));
    }

    @Test
    public void findByEqualLocalData() {
        Node root = new RSQLParser().parse("birthday==2000-10-17");
        Specification<RSQLFooEntity> accept = root.accept(new GenericRSQLVisitor<RSQLFooEntity>());
        List<RSQLFooEntity> all = fooRepository.findAll(accept);

        assertThat(all, hasSize(1));
        assertThat(tom, isIn(all));
    }

    @Test
    public void findByEqualLocalDataTime() {
        Node root = new RSQLParser().parse("when==2000-10-17T17:50:32");
        Specification<RSQLFooEntity> accept = root.accept(new GenericRSQLVisitor<RSQLFooEntity>());
        List<RSQLFooEntity> all = fooRepository.findAll(accept);

        assertThat(all, hasSize(1));
        assertThat(tom, isIn(all));
    }

    @Test
    public void findByEqualEnum() {
        Node root = new RSQLParser().parse("status==ACTIVE");
        Specification<RSQLFooEntity> accept = root.accept(new GenericRSQLVisitor<RSQLFooEntity>());
        List<RSQLFooEntity> all = fooRepository.findAll(accept);

        assertThat(all, hasSize(1));
        assertThat(tom, isIn(all));
    }

    @Test
    public void findByInEnum() {
        Node root = new RSQLParser().parse("status=in=(ACTIVE,INACTIVE)");
        Specification<RSQLFooEntity> accept = root.accept(new GenericRSQLVisitor<RSQLFooEntity>());
        List<RSQLFooEntity> all = fooRepository.findAll(accept);

        assertThat(all, hasSize(2));
        assertThat(tom, isIn(all));
        assertThat(jerry, isIn(all));
    }

    @Test
    public void findByNotEqualBoolean() {
        Node root = new RSQLParser().parse("isLock!=false");
        Specification<RSQLFooEntity> accept = root.accept(new GenericRSQLVisitor<RSQLFooEntity>());
        List<RSQLFooEntity> all = fooRepository.findAll(accept);

        assertThat(all, hasSize(1));
        assertThat(tom, isIn(all));
    }

    @Test
    public void findByAndOr() {
        Node root = new RSQLParser().parse("name=='this is t*';(isLock==false,status=in=(ACTIVE,INACTIVE))");
        Specification<RSQLFooEntity> accept = root.accept(new GenericRSQLVisitor<RSQLFooEntity>());
        List<RSQLFooEntity> all = fooRepository.findAll(accept);

        assertThat(all, hasSize(0));
    }
}

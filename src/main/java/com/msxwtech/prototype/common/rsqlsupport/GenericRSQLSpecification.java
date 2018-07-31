package com.msxwtech.prototype.common.rsqlsupport;

import cz.jirutka.rsql.parser.ast.ComparisonOperator;
import cz.jirutka.rsql.parser.ast.RSQLOperators;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class GenericRSQLSpecification<T> implements Specification<T> {
    private String property;
    private ComparisonOperator operator;
    private List<String> arguments;

    public GenericRSQLSpecification(String property, ComparisonOperator operator, List<String> arguments) {
        this.property = property;
        this.operator = operator;
        this.arguments = arguments;
    }

    @Override
    public Predicate toPredicate(Root<T> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
        List<Object> args = castArguments(root);
        Object argument = args.get(0);
        if(Objects.equals(operator, RSQLOperators.EQUAL)) {
            if(argument instanceof String) {
                return criteriaBuilder.like(root.get(property), ((String) argument).replace("*", "%"));
            } else if(argument == null) {
                return criteriaBuilder.isNull(root.get(property));
            } else {
                return criteriaBuilder.equal(root.get(property),argument);
            }
        } else if (Objects.equals(operator, RSQLOperators.NOT_EQUAL)) {
            if(argument instanceof String) {
                return criteriaBuilder.notLike(root.get(property), ((String) argument).replace("*", "%"));
            } else if (argument == null) {
                return criteriaBuilder.isNotNull(root.get(property));
            } else {
                return criteriaBuilder.notEqual(root.get(property), argument);
            }
        } else if (Objects.equals(operator, RSQLOperators.GREATER_THAN)) {
            return criteriaBuilder.greaterThan(root.get(property), (Comparable)argument);
        } else if (Objects.equals(operator, RSQLOperators.GREATER_THAN_OR_EQUAL)) {
            return criteriaBuilder.greaterThanOrEqualTo(root.get(property), (Comparable)argument);
        } else if (Objects.equals(operator, RSQLOperators.LESS_THAN)) {
            return criteriaBuilder.lessThan(root.get(property), (Comparable)argument);
        } else if (Objects.equals(operator, RSQLOperators.LESS_THAN_OR_EQUAL)) {
            return criteriaBuilder.lessThanOrEqualTo(root.get(property), (Comparable)argument);
        } else if (Objects.equals(operator, RSQLOperators.IN)) {
            return root.get(property).in(args);
        } else if (Objects.equals(operator, RSQLOperators.NOT_IN)) {
            return criteriaBuilder.not(root.get(property).in(args));
        } else {
            throw new UnsupportedOperationException("can not support operator: " + operator);
        }
    }

    private List<Object> castArguments(Root<T> root) {
        List<Object> result = new ArrayList<>();
        Class<?> javaType = root.get(property).getJavaType();
        arguments.forEach(arg -> {
            if(Objects.equals(javaType, Integer.class) || Objects.equals(javaType, int.class)) {
                result.add(Integer.valueOf(arg));
            } else if (Objects.equals(javaType, Long.class) || Objects.equals(javaType, long.class)) {
                result.add(Long.valueOf(arg));
            } else if (Objects.equals(javaType, Double.class) || Objects.equals(javaType, double.class)) {
                result.add(Double.valueOf(arg));
            } else if (Objects.equals(javaType, Float.class) || Objects.equals(javaType, float.class)) {
                result.add(Float.valueOf(arg));
            } else if (Objects.equals(javaType, Boolean.class) || Objects.equals(javaType, boolean.class)) {
                result.add(Boolean.valueOf(arg));
            } else if (Objects.equals(javaType, LocalDate.class)) {
                result.add(LocalDate.parse(arg, DateTimeFormatter.ISO_DATE));
            } else if (Objects.equals(javaType, LocalDateTime.class)) {
                result.add(LocalDateTime.parse(arg, DateTimeFormatter.ISO_DATE_TIME));
            } else if (javaType.isEnum()) {
                for(Object e : javaType.getEnumConstants()) {
                    if (Objects.equals(arg, enumNameHelp((Enum<?>)e))) {
                        result.add(e);
                        return;
                    }
                }
                throw new IllegalArgumentException(arg + "can not be parse to enum: " + javaType);
            } else if (Objects.equals(javaType, String.class)) {
                result.add(arg);
            } else {
                throw new UnsupportedOperationException("can not support java type" + javaType);
            }
        });
        return result;
    }

    private <E extends Enum<E> > String enumNameHelp(Enum<E> e) {
        return e.name();
    }
}

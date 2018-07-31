package com.msxwtech.prototype.common.exceptions;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Optional;
import java.util.UUID;


@RestControllerAdvice
public class ExceptionHandlerAdvice {
    private Logger logger = LoggerFactory.getLogger("com.msxwtech.prototype");

    @Autowired
    private ModelMapper modelMapper;

    @Value("${spring.application.name}")
    private String service;

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ExceptionDTO emptyResultDataAccessException(EmptyResultDataAccessException e) {
        ExceptionDTO result = modelMapper.map(e, ExceptionDTO.class);
        result.setCorrelationId(UUID.randomUUID().toString());
        result.setTimestamp(System.currentTimeMillis());
        result.setService(service);
        result.setError("Entity Not Found");
        result.setCode(EntityNotFoundException.CODE);

        logger.error(result.toString(), e);
        return result;
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ExceptionDTO entityNotFound(EntityNotFoundException e) {
        ExceptionDTO result = modelMapper.map(e, ExceptionDTO.class);
        result.setCorrelationId(UUID.randomUUID().toString());
        result.setTimestamp(System.currentTimeMillis());
        result.setService(service);
        result.setError("Entity Not Found");

        logger.error(result.toString(), e);
        return result;
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ExceptionDTO dataIntegrityViolationException(DataIntegrityViolationException e) {
        ExceptionDTO result = new ExceptionDTO();
        result.setCorrelationId(UUID.randomUUID().toString());
        result.setTimestamp(System.currentTimeMillis());
        result.setService(service);
        result.setError("Constraints Violation");
        result.setCode("constraints_violation");
        Optional.of(e.getCause()).ifPresent(he -> {
            Optional.of(he.getCause()).ifPresent(sqlE -> {
                result.setMessage(sqlE.getMessage());
            });
        });

        logger.error(result.toString(), e);
        return result;
    }
}

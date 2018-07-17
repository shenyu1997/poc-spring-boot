package yu.shen.pocboot.common.exceptions;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.UUID;


@RestControllerAdvice
public class ExceptionHandlerAdvice {
    private Logger logger = LoggerFactory.getLogger("yu.shen.poc");

    @Autowired
    private ModelMapper modelMapper;

    @Value("${app.name}")
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
}

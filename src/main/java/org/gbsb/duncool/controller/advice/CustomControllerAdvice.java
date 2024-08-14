package org.gbsb.duncool.controller.advice;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpServerErrorException;

@RestControllerAdvice
@Log4j2
public class CustomControllerAdvice {

    @ExceptionHandler(HttpServerErrorException.ServiceUnavailable.class) // status: 503
    public ObjectNode handleUnavailable(HttpServerErrorException.ServiceUnavailable e) {
        ObjectNode res = new ObjectMapper().createObjectNode();
        res.put("message", "DNF_SYSTEM_INSPECT");

        return res;
    }
}

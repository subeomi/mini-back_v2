package org.gbsb.duncool.controller.advice;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.gbsb.duncool.util.SingletonManager;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.server.ResponseStatusException;

@RestControllerAdvice
@RequiredArgsConstructor
@Log4j2
public class CustomControllerAdvice {

    private final ObjectMapper om = SingletonManager.getObjectMapper();

    @ExceptionHandler(HttpServerErrorException.ServiceUnavailable.class) // status: 503
    public ResponseEntity<ObjectNode> handleUnavailable(HttpServerErrorException.ServiceUnavailable e) {
        ObjectNode res = om.createObjectNode();
        res.put("message", "DNF_SYSTEM_INSPECT");
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(res);
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ObjectNode> handleResponseStatusException(ResponseStatusException e) {
        HttpStatusCode code = e.getStatusCode();
        ObjectNode res = om.createObjectNode();

//        Bucket 토큰 과소비하면 투 매니 리퀘스트 status: 429
        if (code == HttpStatusCode.valueOf(429)) {
            res.put("message", "TOO_MANY_REQUESTS");
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body(res);
        }

        // 다른 400대 상태 코드에 대한 처리
        res.put("message", e.getReason());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(res);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ObjectNode> handleGeneralException(Exception e) {
        ObjectNode res = om.createObjectNode();
        res.put("message", "SERVER_ERROR");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(res);
    }
}

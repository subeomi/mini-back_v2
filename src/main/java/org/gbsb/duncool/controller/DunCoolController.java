package org.gbsb.duncool.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.github.bucket4j.Bucket;
import org.gbsb.duncool.controller.advice.CustomControllerAdvice;
import org.gbsb.duncool.service.DuncoolService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/")
@Log4j2
@CrossOrigin
public class DunCoolController {

    private final DuncoolService duncoolService;
    private final CustomControllerAdvice advice;
    private final Bucket bucket;

    @GetMapping("list")
    public JsonNode getCharList(
            @RequestParam String type,
            @RequestParam String keyword) {
        if (bucket.tryConsume(1)) {
            if (type.equals("character")) {
                return duncoolService.getCharList(keyword);
            } else if (type.equals("adventure")) {
                return duncoolService.getCharListByAdventure(keyword);
            } else {
                return null;
            }
        } else {
            throw new ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS, "Too many requests");
        }
    }

    @GetMapping("profile")
    public ObjectNode getProfile(
            @RequestParam String serverId,
            @RequestParam String characterId
    ) {
        if (bucket.tryConsume(1)) {
            return duncoolService.getOneInfo(serverId, characterId);
        } else {
            throw new ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS, "Too many requests");
        }
    }
}

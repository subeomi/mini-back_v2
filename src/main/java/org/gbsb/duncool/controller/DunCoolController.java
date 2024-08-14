package org.gbsb.duncool.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.gbsb.duncool.controller.advice.CustomControllerAdvice;
import org.gbsb.duncool.service.DuncoolService;
import org.springframework.web.bind.annotation.*;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.client.HttpServerErrorException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/")
@Log4j2
@CrossOrigin
public class DunCoolController {

    private final DuncoolService duncoolService;
    private final CustomControllerAdvice advice;

    @GetMapping("list")
    public JsonNode getCharList(
            @RequestParam String type,
            @RequestParam String keyword) {
        try {
            if (type.equals("character")) {
                return duncoolService.getCharList(keyword);
            } else if(type.equals("adventure")){
                return duncoolService.getCharListByAdventure(keyword);
            } else {
                return null;
            }
        } catch (HttpServerErrorException.ServiceUnavailable e) {
            return advice.handleUnavailable(e);
        }
    }

    @GetMapping("profile")
    public ObjectNode getProfile(
            @RequestParam String serverId,
            @RequestParam String characterId
    ) {

        try {
            return duncoolService.getOneInfo(serverId, characterId);
        } catch (HttpServerErrorException.ServiceUnavailable e) {
            return advice.handleUnavailable(e);
        }
    }
}

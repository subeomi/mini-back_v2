package org.gbsb.duncool.api;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.extern.log4j.Log4j2;
import org.gbsb.duncool.dto.CharInfoAdvenDTO;
import org.gbsb.duncool.dto.CharInfoDTO;
import org.gbsb.duncool.mappers.InfoMapper;
import org.gbsb.duncool.service.DuncoolService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
@Log4j2
public class ListTests {

    @Autowired
    private InfoMapper infoMapper;
    @Autowired
    private DuncoolService duncoolService;

    @Test
    public void listTest() {
        List<CharInfoDTO> arr = infoMapper.getCharList("깨시민");
        JsonNode arr2 = duncoolService.getCharList("깨시민");

        log.info("arr: {}", arr);
        log.info("arr2: {}", arr2);

        for(JsonNode json : arr2){
            String characterId = json.path("characterId").asText();
            for(CharInfoDTO dto : arr){
                if(dto.getCharacterId().equals(characterId)){
                    ((ObjectNode) json).put("guildId",dto.getGuildId());
                    ((ObjectNode) json).put("guildName",dto.getGuildName());
                    ((ObjectNode) json).put("adventureName",dto.getAdventureName());
                    ((ObjectNode) json).put("totalPrice",dto.getTotalPrice());
                    ((ObjectNode) json).put("hidden",dto.getHidden());
                }
            }
        }

        log.info("검색결과===============");
        for(JsonNode json : arr2){
            log.info(json);
        }
        log.info("======================");
    }

    @Test
    public void searchByAdventureNameTest(){
        List<CharInfoAdvenDTO> arr = infoMapper.getCharListByAdventure("음낭");

        log.info(arr);
    }
}

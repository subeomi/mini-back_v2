package org.gbsb.duncool.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.gbsb.duncool.dto.ItemInfoDTO;
import org.gbsb.duncool.dto.ItemReinforceDTO;
import org.gbsb.duncool.mappers.EnchantMapper;
import org.gbsb.duncool.mappers.ItemMapper;
import org.gbsb.duncool.service.EnchantService;
import org.gbsb.duncool.service.ItemService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

@SpringBootTest
@RequiredArgsConstructor
@Log4j2
public class ItemTests {

    private final String API_KEY = "5KbFU6jtQwi5dwY7TXUHaSutPZlTM9pQ";
    private final String API_URL = "https://api.neople.co.kr/df/";

    private final int limit = 20;

    @Autowired
    private ItemService itemService;

    @Autowired
    private ItemMapper itemMapper;
    @Autowired
    private RestTemplate restTemplate;

    // 크리쳐 이름으로 검색 후 "알"로 끝나면 itemId를 리턴
    @Test
    public void itemTest1() {

        String itemName = "아라드 윈터 캠프[버프 강화]";
        
        String uri = API_URL + "items?itemName=" + itemName + "&wordType=front&apikey=" + API_KEY;

        ResponseEntity<ObjectNode> res = restTemplate.getForEntity(
                uri,
                ObjectNode.class);

        ObjectNode result = res.getBody();

        JsonNode node = result.path("rows");

        log.info(result);
    }

    @Test
    public void itemTest2() {

        String itemId = "053192083c0cebcbbe414388b51088b5";

        String uri = API_URL + "items/" + itemId + "?apikey=" + API_KEY;

        ResponseEntity<ObjectNode> res = restTemplate.getForEntity(
                uri,
                ObjectNode.class);

        ObjectNode result = res.getBody();

        JsonNode node = result.path("rows");

        log.info(result);
    }

    @Test
    public void itemReinforceTest1() {

        String itemId = "e811141ca001d1d23a1de4818fcae6bc";
        String itemId2 = "053192083c0cebcbbe414388b51088b5";

        itemService.getOneItem(itemId);

        String uri = API_URL + "items/" + itemId + "?apikey=" + API_KEY;

        ResponseEntity<ObjectNode> res = restTemplate.getForEntity(
                uri,
                ObjectNode.class);

        ObjectNode result = res.getBody();

        JsonNode reinforce = null;

        if(result.has("itemReinforceSkill")){
            reinforce = result.path("itemReinforceSkill");
            ItemReinforceDTO dto = ItemReinforceDTO.builder()
                    .itemId(itemId)
                    .reinforceSkill(reinforce.toString())
                    .build();
            itemMapper.insertReinforceSkill(dto);
        }

        log.info(reinforce);
    }

    @Test
    public void itemReinforceTest2() {

        String itemId = "e811141ca001d1d23a1de4818fcae6bc";
        String itemId2 = "053192083c0cebcbbe414388b51088b5";
        
        String 귀검사_남 = "41f1cdc2ff58bb5fdc287be0db2a8df3";

        ItemReinforceDTO dto = itemMapper.getReinforceSkill(itemId);
        ItemReinforceDTO dto2 = itemMapper.getReinforceSkill(itemId2);

        ObjectMapper om = new ObjectMapper();
        ObjectNode on = om.createObjectNode();

        JsonNode dtoArr = null;
        try {
            dtoArr = om.readTree(dto.getReinforceSkill());
            log.info("dtoArr: " + dtoArr);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        for(JsonNode arr : dtoArr){
            log.info("arr: " + arr);
            if (arr.path("jobId").isNull() && arr.path("jobName").asText().equals("공통")) {
                on.set("reinforce", arr);
            } else if(arr.path("jobId").asText().equals(귀검사_남)){
                on.set("reinforce", arr);
            }
        }

        log.info("on: " + on);
    }

}

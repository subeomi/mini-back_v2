package org.gbsb.duncool.api;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.extern.log4j.Log4j2;
import org.gbsb.duncool.dto.CharAllDTO;
import org.gbsb.duncool.dto.CharInfoDTO;
import org.gbsb.duncool.mappers.InfoMapper;
import org.gbsb.duncool.util.SingletonManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@SpringBootTest
@Log4j2
public class CheckTests {

    // 마법부여 X
    private final String 타멜 = "cbf40d3eebc910c002a78c0e6a9478ef";
//    장비 X
    private final String 빨간바람 = "6c0ee675cc624ae0b9d10bb8940f2a8c";
    
    private final String 깨시민 = "ba24a0025ad3fb1c72d26f2a079f7cc0";
    private final String API_KEY = "5KbFU6jtQwi5dwY7TXUHaSutPZlTM9pQ";
    private final String API_URL = "https://api.neople.co.kr/df/";
    private String 카시야스 = "casillas";

    @Autowired
    private RestTemplate restTemplate;

    @Test
    public void checkTest1(){
        String uri = API_URL + "servers/" + 카시야스 + "/characters/" +
                타멜 + "/equip/equipment?&apikey=" + API_KEY;

        ObjectMapper om = new ObjectMapper();

        ResponseEntity<ObjectNode> res = restTemplate.getForEntity(
                uri,
                ObjectNode.class);

        ObjectNode result = res.getBody();
        log.info(result);
        JsonNode equip = result.path("equipment");
        log.info(equip.size()+"개 부위");

//        LinkedHashSet은 순서를 보장함
        Set<String> diffEquipSlot = new LinkedHashSet<>(SingletonManager.EQUIP_PARTS);
        Set<String> diffEnchantSlot = new LinkedHashSet<>();
        log.info(diffEquipSlot);
        for (JsonNode item : equip) {
            log.info(item);
            if(item.path("enchant").isEmpty() || item.path("enchant").isNull()){
                diffEnchantSlot.add(item.path("slotName").asText());
            }
            diffEquipSlot.remove(item.path("slotName").asText());
        }
        if (diffEquipSlot.size() > 0) {
            log.info("누락된 장비가 있습니다. : " + diffEquipSlot);
        }
        if (diffEnchantSlot.size() > 0) {
            log.info("장비에 누락된 마법부여가 있습니다. : "+diffEnchantSlot);
        }
    }
}

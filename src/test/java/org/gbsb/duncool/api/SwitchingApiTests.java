package org.gbsb.duncool.api;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.gbsb.duncool.dto.PriceInfoDTO;
import org.gbsb.duncool.service.AuctionService;
import org.gbsb.duncool.service.ItemService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

@SpringBootTest
@RequiredArgsConstructor
@Log4j2
public class SwitchingApiTests {

    private final String API_KEY = "5KbFU6jtQwi5dwY7TXUHaSutPZlTM9pQ";
    private final String DNF_URL = "https://api.neople.co.kr/df/";

    private String 카시야스 = "casillas";
    private String 깨시민 = "67d387926ea1a53b57f64ee0a24893b2";
//    private String 깨시민 = "ba24a0025ad3fb1c72d26f2a079f7cc0";
    private String 고등오 = "67d387926ea1a53b57f64ee0a24893b2";

    @Autowired
    private AuctionService auctionService;

    @Autowired
    private ItemService itemService;

    @Autowired
    private RestTemplate restTemplate;

    // 프라이빗은 테스트X, 퍼블릭에서 코드를 완성한 다음에 메소드로 나눌것...
    @Test
    public void switchingTest1() {
        JsonNode equip = switchingEquipTest1();
        JsonNode avatar = switchingAvatarTest1();
        JsonNode creature = switchingCreatureTest1();

        ObjectMapper om = new ObjectMapper();

        JsonNode eqPrice = om.createObjectNode();
        JsonNode avPrice = om.createObjectNode();
        JsonNode crPrice = om.createObjectNode();

        for (JsonNode eq : equip) {
//            log.info("equip: " + eq);
            String itemName = eq.path("itemName").asText();
            String itemId = eq.path("itemId").asText();
            log.info("itemName: " + itemName);

            PriceInfoDTO eqAuc = auctionService.getAucItemId(itemId);
//            log.info("equip auction: "+eqAuc);
        }
        for (JsonNode av : avatar) {
//            log.info("avatar: " + av);
            String itemName = av.path("itemName").asText();
            String itemId = av.path("itemId").asText();

            String switEmblem = av.path("emblems").get(0).path("itemId").asText();
            JsonNode em = av.path("emblems");
            log.info(em);
            log.info("엠블렘: " + switEmblem);
            PriceInfoDTO emblemAuc = auctionService.getAucItemId(switEmblem);
            log.info("itemName: " + itemName);
            PriceInfoDTO avAuc = auctionService.getAucItemId(itemId);
            itemService.checkItemInfo(itemId);
//            log.info("avatar auction: "+avAuc);
        }
        for (JsonNode cr : creature) {
//            log.info("creature" + cr);
            String itemName = cr.path("itemName").asText();
            String itemId = cr.path("itemId").asText();

            log.info("itemName: " + itemName);
//            JsonNode crAuc = auctionService.getAucItemEgg(itemName);
//            log.info("creature auction: "+crAuc);
            itemService.checkItemInfo(itemId);
        }

        log.info("equip: " + equip);
        log.info("avatar: " +avatar);
        log.info("creature: " +creature);
    }

    @Test
    private JsonNode switchingEquipTest1() {
        String uri = DNF_URL + "servers/" + 카시야스 + "/characters/" +
                깨시민 + "/skill/buff/equip/equipment?apikey=" + API_KEY;

        ResponseEntity<ObjectNode> res = restTemplate.getForEntity(
                uri,
                ObjectNode.class);

        ObjectNode result = res.getBody();
        JsonNode equip = result.path("skill").path("buff").path("equipment");

        return equip;
    }

    @Test
    private JsonNode switchingAvatarTest1() {
        String uri = DNF_URL + "servers/" + 카시야스 + "/characters/" +
                깨시민 + "/skill/buff/equip/avatar?apikey=" + API_KEY;

        ResponseEntity<ObjectNode> res = restTemplate.getForEntity(
                uri,
                ObjectNode.class);

        ObjectNode result = res.getBody();
        JsonNode ava = result.path("skill").path("buff").path("avatar");

        return ava;
    }

    @Test
    private JsonNode switchingCreatureTest1() {
        String uri = DNF_URL + "servers/" + 카시야스 + "/characters/" +
                깨시민 + "/skill/buff/equip/creature?apikey=" + API_KEY;

        ResponseEntity<ObjectNode> res = restTemplate.getForEntity(
                uri,
                ObjectNode.class);

        ObjectNode result = res.getBody();
        JsonNode creature = result.path("skill").path("buff").path("creature");

        return creature;
    }
}

package org.gbsb.duncool.api;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.gbsb.duncool.dto.PriceInfoDTO;
import org.gbsb.duncool.service.AuctionService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

@SpringBootTest
@RequiredArgsConstructor
@Log4j2
public class CreatureApiTests {

    private final String API_KEY = "5KbFU6jtQwi5dwY7TXUHaSutPZlTM9pQ";
    private final String API_URL = "https://api.neople.co.kr/df/";

    private String 카시야스 = "casillas";
    private String 깨시민 = "ba24a0025ad3fb1c72d26f2a079f7cc0";

    @Autowired
    private AuctionService service;

    @Autowired
    private RestTemplate restTemplate;

    @Test
    public void getCreatureTest() {
        String uri = API_URL + "servers/" + 카시야스 + "/characters/" +
                깨시민 + "/equip/creature?apikey=" + API_KEY;

        ResponseEntity<ObjectNode> res = restTemplate.getForEntity(
                uri,
                ObjectNode.class);

        ObjectNode result = res.getBody();

        String jobName = result.path("jobName").asText();

        log.info("result: " + result);

        JsonNode creature = result.path("creature");
        JsonNode artifact = creature.path("artifact");

        log.info("creature: " + creature);
        String itemId = creature.path("itemId").asText();
        String itemName = creature.path("itemName").asText();

        // 아이템 시세검색(최근 성사된 거래목록)은 파라미터가 itemId 또는 itemName *둘 중 하나*가 있어야한다
//        String auctionSoldUri = API_URL + "auction-sold?itemName=" +
//                itemName + "&apikey=" + API_KEY;
        String eggId = getEggItemId(itemName);
//        PriceInfoDTO crePrice = service.getAucItemId(eggId);
//        log.info("크리쳐: "+crePrice);

        // 아티팩트
        for (JsonNode arr : artifact) {
//            int artiPrice = 0;
//            String artiId = arr.path("itemId").asText();
//
//            PriceInfoDTO artiResult = service.getAucItemId(artiId);
//            log.info("아티팩트: " + artiResult);
        }
    }

    private String getEggItemId(String creatureName) {
        String aucItemId = null;

        if (creatureName.contains("SD 흰 구름 전령 에를리히")) {
            aucItemId = "1ca719543274a0ae0b12b478d88c0448";
        } else if (creatureName.contains("SD 땅지기 슈므")) {
            aucItemId = "b3af05ec93a2832c0b47fde15e3da5f3";
        } else if (creatureName.contains("SD 야가미 이오리")) {
            aucItemId = "07bf672648dff135d37bccfa30cddf6a";
        } else if (creatureName.contains("SD 시라누이 마이")) {
            aucItemId = "9c452fbfeecad0e49292928baeb7e2c0";
        }else if (creatureName.contains("순백의 나비 공주")) {
            aucItemId = "701574f4768183cd7d8fee2774de2a93";
        }else if (creatureName.contains("축제의 여왕 페리아")) {
            aucItemId = "87a131e3e8ab68b0e3ff5ea80d9e8975";
        }

        return aucItemId;
    }
}
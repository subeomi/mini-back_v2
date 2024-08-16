package org.gbsb.duncool.api;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.gbsb.duncool.dto.ItemInfoDTO;
import org.gbsb.duncool.service.AuctionService;
import org.gbsb.duncool.service.ItemService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

@SpringBootTest
@RequiredArgsConstructor
@Log4j2
public class AvatarApiTests {

    @Value("${dnf.api.key}")
    private String API_KEY;
    @Value("${dnf.api.url}")
    private String API_URL;

    private String 카시야스 = "casillas";
    private String 깨시민 = "ba24a0025ad3fb1c72d26f2a079f7cc0";

    @Autowired
    private ItemService itemService;
    @Autowired
    private AuctionService auctionService;
    @Autowired
    private RestTemplate restTemplate;

    @Test
    public void getAvatar() {
        String uri = API_URL + "servers/" + 카시야스 + "/characters/" +
                깨시민 + "/equip/avatar?apikey=" + API_KEY;

        ResponseEntity<ObjectNode> res = restTemplate.getForEntity(
                uri,
                ObjectNode.class);

        ObjectNode result = res.getBody();
        String myJobName = result.path("jobName").asText();

//        log.info("result: " + result);

        JsonNode avatarArr = result.path("avatar");
        for (JsonNode arr : avatarArr) {
//            log.info(arr);
            String itemId = arr.path("itemId").asText();
            String itemName = arr.path("itemName").asText();
            // 클론 형상아바타
            JsonNode clone = arr.path("clone");

            // 아이템 시세검색(최근 성사된 거래목록)은 파라미터가 itemId 또는 itemName *둘 중 하나*가 있어야한다
            auctionService.getAucItemId(itemId);
            ItemInfoDTO dto = itemService.getOneItem(itemId);
            log.info("item: " + dto);

            // 클론 형상아바타 검색
            String cloneId = clone.path("itemId").asText();
            String cloneName = clone.path("itemName").asText();

            if (cloneName != null) {
                int cnt = 0;
//                log.info("itemId: " + cloneId);
                auctionService.getAucItemId(cloneId);
                if (cloneId != null && !cloneId.equals("null")) {
                    itemService.getOneItem(cloneId);
                }

            }
        }
    }
}

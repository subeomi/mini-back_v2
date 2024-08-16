package org.gbsb.duncool.api;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.extern.log4j.Log4j2;
import org.gbsb.duncool.dto.EnchantDTO;
import org.gbsb.duncool.dto.PriceInfoDTO;
import org.gbsb.duncool.mappers.EnchantMapper;
import org.gbsb.duncool.service.AuctionService;
import org.gbsb.duncool.service.EnchantService;
import org.gbsb.duncool.service.ItemService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@SpringBootTest
@Log4j2
public class EnchantTests {

    @Value("${dnf.api.key}")
    private String API_KEY;
    @Value("${dnf.api.url}")
    private String API_URL;

    private String 카시야스 = "casillas";
    private String 바칼 = "bakal";
    private String 카인 = "cain";

    private final String 븝프 = "4b982ea11e3fc13bbe4100cae2cc5c9e";
    private final String 븜미맨 = "33ef61758bc45e20ac5082f8009e8382"; // 서버 바칼
    private final String 깨시민 = "ba24a0025ad3fb1c72d26f2a079f7cc0";
    private final String 핑핑이수프 = "d0dde5fcd22636b9e6de2f2fa81da722"; // 서버 카인

    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private AuctionService auctionService;
    @Autowired
    private ItemService itemService;
    @Autowired
    private EnchantMapper enchantMapper;
    @Autowired
    private EnchantService enchantService;


    @Test
    public void addEnchantTest() {

        List<String> enchantArr = Arrays.asList(
                "멸절의 폭룡왕 바칼 카드", "블루호크 선장 버디 카드", "사라 웨인 카드",
                "안개의 감시자 브림 카드", "안개의 사제 클라디스 카드", "사무치는 냉룡 스카사 카드",
                "심연을 걷는 자 카드", "평온한 로절린드 카드", "빛나는 다이앤 카드",
                "아홉 꼬리 블로나 카드", "큰 어른 루톤 카드", "연합 사령관 이리네 카드",
                "뒤쫓는 자 제논 카드", "코드네임 게이볼그 카드", "불신위괴 라르고 카드",
                "요기를 머금은 요무무 카드", "부패하는 사룡 스피라찌 카드", "불안정한 균열 카드",
                "불길한 사벨리 카드", "숲의 전언자 카밀라 카드", "기억의 도서관지기 카드",
                "흰 구름 감시자 라르고 카드", "불의 숨결을 내는 자 카드", "화룡 애쉬코어 카드",
                "이터널 플레임 대장 스타크 카드", "이슬을 감춘 자 카드", "분노하는 안개 브림 카드",
                "퀸 디스트로이어 카드", "파괴하는 광룡 히스마 카드", "한기의 게르다 카드",
                "진룡 이트레녹 카드", "GB-1 햅스 카드", "요룡 님파 카드",
                "흑룡 네이저 카드", "GB-4 디리겐트 카드", "금룡 느마우그 카드", "1대대 대장 무적자 유진 카드"
        );

        for (String itemName : enchantArr) {
            log.info("==================");
            log.info("itemName: " + itemName);

            enchantService.createEnchant(itemName);

//            PriceInfoDTO priceDTO = auctionService.getAucItemId(itemId);
//            log.info("price: " + priceDTO);
        }

    }

    @Test
    public void searchEnchantTest() {
        String itemName = "금룡 느마우그 카드";
        String nameUri = API_URL + "items?itemName=" + itemName + "&apikey=" + API_KEY;

        ResponseEntity<ObjectNode> nameRes = restTemplate.getForEntity(
                nameUri,
                ObjectNode.class);

        ObjectNode nameResult = nameRes.getBody();
//        log.info(nameResult);
        JsonNode nameRows = nameResult.path("rows").get(0);

        String itemId = nameRows.path("itemId").asText();

        String idUri = API_URL + "items/" + itemId + "?apikey=" + API_KEY;

        ResponseEntity<ObjectNode> idRes = restTemplate.getForEntity(
                idUri,
                ObjectNode.class);

        ObjectNode idResult = idRes.getBody();

        JsonNode itemStatus = idResult.path("itemStatus");
        int fame = 0;

        for (JsonNode item : itemStatus) {
            if (item.path("name").asText().contains("모험가 명성")) {
                fame = item.path("value").asInt();
                break;
            }
        }

        log.info("itemName: " + itemName);
        log.info("idResult: " + idResult);
        log.info("fame: " + fame);
        JsonNode slots = idResult.path("cardInfo").path("slots");
        JsonNode enchant = idResult.path("cardInfo").path("enchant");

//        log.info("idResult: "+idResult);
//        log.info(itemService.getOneItem(itemId));
        log.info(slots.size());

        for (JsonNode enc : enchant) {
            String upgrade = enc.path("upgrade").asText();
            log.info("(+" + upgrade + "): " + enc);
//
//            ((ObjectNode) enc).remove("upgrade");
//            EnchantDTO dto = EnchantDTO.builder()
//                    .upgrade(upgrade)
//                    .itemId(itemId)
//                    .itemName(itemName)
//                    .enchant(enc.toString())
//                    .build();

        }

        for (JsonNode slot : slots) {
            log.info("slot: " + slot);
//            String slotName = slot.path("slotName").asText();
//            EnchantDTO dto = EnchantDTO.builder()
//                    .slot(slotName)
//                    .itemId(itemId)
//                    .build();

        }
    }

}

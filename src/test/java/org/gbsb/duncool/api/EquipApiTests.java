package org.gbsb.duncool.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.gbsb.duncool.dto.EnchantDTO;
import org.gbsb.duncool.dto.ItemInfoDTO;
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

@SpringBootTest
@RequiredArgsConstructor
@Log4j2
public class EquipApiTests {

    @Value("${dnf.api.key}")
    private String API_KEY;
    @Value("${dnf.api.url}")
    private String API_URL;

    private String 카시야스 = "casillas";
    private String 깨시민 = "ba24a0025ad3fb1c72d26f2a079f7cc0";

    @Autowired
    private ItemService service;
    @Autowired
    private AuctionService auctionService;
    @Autowired
    private EnchantService enchantService;

    @Autowired
    private EnchantMapper enchantMapper;
    @Autowired
    private RestTemplate restTemplate;

    @Test
    public void getEquip() {
        String uri = API_URL + "servers/" + 카시야스 + "/characters/" +
                깨시민 + "/equip/equipment?&apikey=" + API_KEY;

        ObjectMapper om = new ObjectMapper();

        ResponseEntity<ObjectNode> res = restTemplate.getForEntity(
                uri,
                ObjectNode.class);

        ObjectNode result = res.getBody();

        JsonNode equipArr = result.path("equipment");

        log.info("result: " + equipArr);
        for (JsonNode equip : equipArr) {
            String 부위 = equip.path("slotName").asText();
            String itemId = equip.path("itemId").asText();

            JsonNode enchant = equip.path("enchant");
            String 마부 = enchant.toString();
            log.info(부위 + "마부: " + 마부);

            EnchantDTO dto = EnchantDTO.builder()
                    .slot(부위)
                    .enchant(마부)
                    .build();

            dto = enchantService.getEnchant(dto);
            log.info("마부 정보: " + dto);
            if (dto != null) {
                PriceInfoDTO priceInfoDTO = auctionService.getAucEnchantDTO(dto);
                log.info(priceInfoDTO);
            }
        }
    }

    @Test
    public void getPriceByEquipEnchant() {
        String uri = API_URL + "servers/" + 카시야스 + "/characters/" +
                깨시민 + "/equip/equipment?&apikey=" + API_KEY;

        ObjectMapper om = new ObjectMapper();

        ResponseEntity<ObjectNode> res = restTemplate.getForEntity(
                uri,
                ObjectNode.class);

        JsonNode result = res.getBody();

        result = auctionService.getEquipPrice(result);

        JsonNode equip = result.path("equipment");
        for (JsonNode eq : equip) {
            String 부위 = eq.path("slotName").asText();
            int 마부값 = eq.path("enchantPrice").asInt();
            log.info(eq);
            log.info("부위: " + 부위 + ", 마부값: " + 마부값);
        }
    }

    @Test
    public void checkItemInfoTest(String itemId) {
        String uri = API_URL + "items/" + itemId + "?apikey=" + API_KEY;

        ResponseEntity<ObjectNode> res = restTemplate.getForEntity(
                uri,
                ObjectNode.class);

        ObjectNode result = res.getBody();

        log.info(result);
    }
}

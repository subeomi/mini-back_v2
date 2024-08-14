package org.gbsb.duncool.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.gbsb.duncool.dto.EnchantDTO;
import org.gbsb.duncool.dto.ItemInfoDTO;
import org.gbsb.duncool.mappers.EnchantMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@Log4j2
@RequiredArgsConstructor
public class EnchantServiceImpl implements EnchantService{

    @Value("${dnf.api.key}")
    private String apiKey;
    @Value("${dnf.api.url}")
    private String apiUrl;

    private final ItemService itemService;
    private final EnchantMapper enchantMapper;
    private final RestTemplate restTemplate;

    @Override
    public EnchantDTO getEnchant(EnchantDTO dto) {

        dto = enchantMapper.getOneEnchant(dto);

        return dto;
    }

    // 카드 이름으로 거래가능 카드 itemId와 스탯정보 입력
    @Override
    public EnchantDTO createEnchant(String itemName) {

        String nameUri = apiUrl + "items?itemName="+itemName+"&apikey=" + apiKey;

        ResponseEntity<ObjectNode> nameRes = restTemplate.getForEntity(
                nameUri,
                ObjectNode.class);

        ObjectNode nameResult = nameRes.getBody();
        log.info(nameResult);
        JsonNode nameRows = nameResult.path("rows").get(0);

        String itemId = nameRows.path("itemId").asText();

        String idUri = apiUrl + "items/"+itemId+"?apikey=" + apiKey;

        ResponseEntity<ObjectNode> idRes = restTemplate.getForEntity(
                idUri,
                ObjectNode.class);

        ObjectNode idResult = idRes.getBody();

        JsonNode slots = idResult.path("cardInfo").path("slots");
        JsonNode enchant = idResult.path("cardInfo").path("enchant");

//        log.info("idResult: "+idResult);
        log.info(itemService.getOneItem(itemId));
        log.info(slots.size());

        for (JsonNode enc : enchant) {
            String upgrade = enc.path("upgrade").asText();

            ((ObjectNode) enc).remove("upgrade");
            log.info("(+"+upgrade+"): "+enc);
            EnchantDTO dto = EnchantDTO.builder()
                    .upgrade(upgrade)
                    .itemId(itemId)
                    .itemName(itemName)
                    .enchant(enc.toString())
                    .build();

            enchantMapper.insertEnchantInfo(dto);
        }

        for (JsonNode slot : slots) {
            log.info("slot: " + slot);
            String slotName = slot.path("slotName").asText();
            EnchantDTO dto = EnchantDTO.builder()
                    .slot(slotName)
                    .itemId(itemId)
                    .build();

            enchantMapper.insertEnchantSlot(dto);
        }

        return null;
    }

}

package org.gbsb.duncool.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.gbsb.duncool.dto.EnchantDTO;
import org.gbsb.duncool.dto.ItemInfoDTO;
import org.gbsb.duncool.mappers.EnchantMapper;
import org.gbsb.duncool.service.data.EnchantDataService;
import org.gbsb.duncool.util.SingletonManager;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

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
    private final EnchantDataService enchantDataService;
    private final RestTemplate restTemplate;

    private final ObjectMapper om = SingletonManager.getObjectMapper();

    @Override
    public EnchantDTO getEnchant(EnchantDTO dto) {

//        dto = enchantMapper.getOneEnchant(dto);
//
//        return dto;

        List<EnchantDTO> enchantList = enchantMapper.getEnchantBySlot(dto.getSlot());

        // JSON 비교 로직 (Java에서 수행)
        for (EnchantDTO enchant : enchantList) {
            if (areJsonEqual(dto.getEnchant(), enchant.getEnchant())) {
                return enchant;
            }
        }

        return null; // 일치하는 마법부여가 없는 경우
    }

    // JSON 비교 함수
    private boolean areJsonEqual(String json1, String json2) {
        try {
            JsonNode tree1 = om.readTree(json1);
            JsonNode tree2 = om.readTree(json2);

            tree1 = sortJsonNode(tree1);
            tree2 = sortJsonNode(tree2);

            return tree1.equals(tree2);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    private JsonNode sortJsonNode(JsonNode node) {
        if (node.isObject()) {
            ObjectNode sortedNode = om.createObjectNode();
            node.fieldNames().forEachRemaining(field -> sortedNode.set(field, sortJsonNode(node.get(field))));
            return sortedNode;
        } else if (node.isArray()) {
            ArrayNode sortedArray = om.createArrayNode();
            List<JsonNode> nodeList = new ArrayList<>();
            node.forEach(nodeList::add);
            nodeList.sort(Comparator.comparing(JsonNode::toString)); // toString()을 사용하여 정렬
            nodeList.forEach(sortedArray::add);
            return sortedArray;
        } else {
            return node;
        }
    }

    // 카드 이름으로 거래가능 카드 itemId와 스탯정보 입력
    @Override
    @Transactional
    public EnchantDTO createEnchant(String itemName) {

        if(itemName.endsWith("카드")){
            insertCardInfo(itemName);
        } else if(itemName.endsWith("보주")){
            insertOrbInfo(itemName);
        } else {
            log.info("it's not card/orb: " + itemName);
        }

        return null;
    }

//    아이템 검색 결과가 1인 카드의 경우 적용
    private void insertCardInfo(String itemName){

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

            enchantDataService.insertEnchantInfoData(dto);
        }

        for (JsonNode slot : slots) {
            log.info("slot: " + slot);
            String slotName = slot.path("slotName").asText();
            EnchantDTO dto = EnchantDTO.builder()
                    .slot(slotName)
                    .itemId(itemId)
                    .build();

            enchantDataService.insertEnchantSlotData(dto);
        }
    }
    
//    배열 길이가 2 이상일 수 있는 보주의 경우
    private void insertOrbInfo(String itemName){
        String nameUri = apiUrl + "items?itemName=" + itemName + "&wordType=front&apikey=" + apiKey;

        ResponseEntity<ObjectNode> nameRes = restTemplate.getForEntity(
                nameUri,
                ObjectNode.class);

        ObjectNode nameResult = nameRes.getBody();
        log.info(nameResult);
//        카드와 다르게 보주는 배열을 가져와서 전부 적용시키도록 함
        JsonNode nameRows = nameResult.path("rows");

        for(JsonNode nameRow : nameRows) {
            String itemId = nameRow.path("itemId").asText();

            String idUri = apiUrl + "items/" + itemId + "?apikey=" + apiKey;

            ResponseEntity<ObjectNode> idRes = restTemplate.getForEntity(
                    idUri,
                    ObjectNode.class);

            ObjectNode idResult = idRes.getBody();

            JsonNode slots = idResult.path("cardInfo").path("slots");
            JsonNode enchant = idResult.path("cardInfo").path("enchant");

//            명성 156 초과시 컷
            int fameValue = idResult.has("fame") ? idResult.path("fame").asInt() : 0;

            if(fameValue > 156){
                continue;
            }

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

                enchantDataService.insertEnchantInfoData(dto);
            }

            for (JsonNode slot : slots) {
                log.info("slot: " + slot);
                String slotName = slot.path("slotName").asText();
                EnchantDTO dto = EnchantDTO.builder()
                        .slot(slotName)
                        .itemId(itemId)
                        .build();

                enchantDataService.insertEnchantSlotData(dto);
            }
        }
    }

}

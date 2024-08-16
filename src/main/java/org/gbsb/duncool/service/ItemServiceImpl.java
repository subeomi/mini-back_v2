package org.gbsb.duncool.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.gbsb.duncool.dto.ItemInfoDTO;
import org.gbsb.duncool.dto.ItemReinforceDTO;
import org.gbsb.duncool.mappers.ItemMapper;
import org.gbsb.duncool.util.SingletonManager;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@Log4j2
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    @Value("${dnf.api.key}")
    private String apiKey;
    @Value("${dnf.api.url}")
    private String apiUrl;
    
    private final ItemMapper itemMapper;
    private final RestTemplate restTemplate;
    private final ObjectMapper om = SingletonManager.getObjectMapper();

    @Override
    public ItemInfoDTO getOneItem(String itemId) {

        if (itemId == null || itemId.equals("null") || itemId.isEmpty()) return null;
//        log.info("get one item id: " + itemId);

        ItemInfoDTO dto = itemMapper.getOneItem(itemId);
        if (dto == null || dto.getItemRarity().isEmpty() || dto.getItemFame() == 0) {
            String uri = apiUrl + "items/" + itemId + "?apikey=" + apiKey;
            ResponseEntity<ObjectNode> res = restTemplate.getForEntity(
                    uri,
                    ObjectNode.class);

            ObjectNode result = res.getBody();

            JsonNode itemStatus = result.path("itemStatus");
            int fame = 0;

            for (JsonNode item : itemStatus) {
                if (item.path("name").asText().contains("모험가 명성")) {
                    fame = item.path("value").asInt();
                    break;
                }
            }

            dto = om.convertValue(result, ItemInfoDTO.class);
            dto.setItemFame(fame);
//            log.info(dto);

            itemMapper.insertItemInfo(dto);
        }
//        log.info("item: " + dto);

        return dto;
    }

    @Override
    public String getIdByName(String itemName) {
        return null;
    }

    @Override
    public void checkItemInfo(String itemId) {
        String uri = apiUrl + "items/" + itemId + "?apikey=" + apiKey;

        ResponseEntity<ObjectNode> res = restTemplate.getForEntity(
                uri,
                ObjectNode.class);

        ObjectNode result = res.getBody();

        log.info("item info: "+result);
    }

    @Override
    public JsonNode getReinforceItem(String itemId, String jobId) {

        ItemReinforceDTO dto = itemMapper.getReinforceSkill(itemId);
        JsonNode jn = om.createObjectNode();

        if (dto == null) {
            getOneItem(itemId);

            String uri = apiUrl + "items/" + itemId + "?apikey=" + apiKey;

            ResponseEntity<ObjectNode> res = restTemplate.getForEntity(
                    uri,
                    ObjectNode.class);

            ObjectNode result = res.getBody();

            if(result.has("itemReinforceSkill")){
                JsonNode reinforce = result.path("itemReinforceSkill");
                dto = ItemReinforceDTO.builder()
                        .itemId(itemId)
                        .reinforceSkill(reinforce.toString())
                        .build();
                itemMapper.insertReinforceSkill(dto);
            }
        }

        if (dto != null) {
            JsonNode reinforceNode = null;
            try {
                reinforceNode = om.readTree(dto.getReinforceSkill());
//                log.info("dtoArr: " + reinforceNode);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }

            for(JsonNode arr : reinforceNode){
//                log.info("arr: " + arr);
                if (arr.path("jobId").isNull() && arr.path("jobName").asText().equals("공통")) {
                    ((ObjectNode) jn).set("reinforce", arr);
                } else if(arr.path("jobId").asText().equals(jobId)){
                    ((ObjectNode) jn).set("reinforce", arr);
                }
            }
        }

        return jn;
    }
}

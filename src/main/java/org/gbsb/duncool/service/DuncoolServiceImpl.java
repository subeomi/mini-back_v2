package org.gbsb.duncool.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.RequiredArgsConstructor;
import org.gbsb.duncool.dto.CharAllDTO;
import org.gbsb.duncool.dto.CharInfoAdvenDTO;
import org.gbsb.duncool.dto.CharInfoDTO;
import org.gbsb.duncool.mappers.InfoMapper;
import org.gbsb.duncool.util.SingletonManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import lombok.extern.log4j.Log4j2;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Log4j2
@Service
@RequiredArgsConstructor
public class DuncoolServiceImpl implements DuncoolService {

    @Value("${dnf.api.key}")
    private String apiKey;
    @Value("${dnf.api.url}")
    private String apiUrl;

    private final EquipService equipService;
    private final AuctionService auctionService;

    private final InfoMapper mapper;

    private final ObjectMapper om = SingletonManager.getObjectMapper();

    // 간단한 사용법

    // 1. RestTemplate 객체를 만들고
    @Autowired
    private RestTemplate restTemplate;

    @Override
    public JsonNode getCharList(String characterName) {

        characterName.trim();

        String wordType = "";
        if (characterName.length() <= 1) {
            wordType = "match";
        } else {
            wordType = "full";
        }

        String uri = apiUrl + "servers/all/characters?characterName=" +
                characterName
                + "&wordType=" + wordType + "&apikey=" + apiKey;

        ResponseEntity<ObjectNode> responseEntity = restTemplate.getForEntity(
                uri,
                ObjectNode.class);


        ObjectNode result = responseEntity.getBody();

        List<CharInfoDTO> dbArr = mapper.getCharList(characterName);
        JsonNode arr = result.path("rows");

        for(JsonNode json : arr){
            String characterId = json.path("characterId").asText();
            for(CharInfoDTO dto : dbArr){
                if(dto.getCharacterId().equals(characterId)){
                    ((ObjectNode) json).put("guildId",dto.getGuildId());
                    ((ObjectNode) json).put("guildName",dto.getGuildName());
                    ((ObjectNode) json).put("adventureName",dto.getAdventureName());
                    ((ObjectNode) json).put("totalPrice",dto.getTotalPrice());
                    ((ObjectNode) json).put("hidden",dto.getHidden());
                }
            }
        }

        log.info("get list: " + arr);

        return arr;
    }

    @Override
    public JsonNode getCharListByAdventure(String adventureName) {

        adventureName.trim();

        List<CharInfoAdvenDTO> lst = mapper.getCharListByAdventure(adventureName);

        ArrayNode arr = om.createArrayNode();

        for(CharInfoAdvenDTO dto : lst) {
            JsonNode node = om.valueToTree(dto);
            arr.add(node);
        }

        log.info("get list: " + arr);

        return arr;
    }

    @Override
    public ObjectNode getOneInfo(String serverId, String characterId) {

        log.info("get one info...");
        CharAllDTO dto = mapper.getCharAll(characterId);

        ObjectNode result = om.createObjectNode();

        ObjectNode data = null;
        ObjectNode equipment = null;
        ObjectNode avatar = null;
        ObjectNode creature = null;
        ObjectNode switching = null;

        String udate = null;
        Long minuteDiff = null;

        if (dto != null) {
            udate = dto.getUdate();
            log.info("dto: " + dto);

            LocalDateTime now = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            LocalDateTime dateTime = LocalDateTime.parse(udate, formatter);
            log.info("udate: " + udate + ", dateTime: " + dateTime);

            Duration duration = Duration.between(dateTime, now);
            minuteDiff = duration.toMinutes();
            log.info("minuteDiff: " + minuteDiff);
        }

        if (dto == null || minuteDiff >= 1) {
            return getOneInfoFromAPI(serverId, characterId, dto);
        } else if (dto != null) {
            log.info("isDTO");
//            log.info("dto.getCreature(): {}", dto.getCreature());
            try {
                data = (ObjectNode) om.readTree(dto.getData());
                equipment = (ObjectNode) om.readTree(dto.getEquip());
                avatar = (ObjectNode) om.readTree(dto.getAvatar());
                creature = (ObjectNode) om.readTree(dto.getCreature());
                switching = (ObjectNode) om.readTree(dto.getSwitching());
            } catch (JsonProcessingException e) {
                e.printStackTrace();
                return null;
            }
            log.info("data" + data, equipment);
        }

        result.set("equipment", equipment);
        result.set("data", data);
        result.set("avatar", avatar);
        result.set("creature", creature);
        result.set("switching", switching);

        return result;
    }

    private ObjectNode getOneInfoFromAPI(String serverId, String characterId, CharAllDTO dto) {

        ObjectNode result = om.createObjectNode();

        ObjectNode data = getCharInfo(serverId, characterId);
        ObjectNode equipment = getEquipment(serverId, characterId);
        ObjectNode avatar = getAvatar(serverId, characterId);
        ObjectNode creature = getCreature(serverId, characterId);

        String jobId = data.path("jobId").asText();
        String jobGrowId = data.path("jobGrowId").asText();

        ObjectNode switching = getSwitching(serverId, characterId, jobId);

        LocalDateTime now = LocalDateTime.now();
        String udate = now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        String characterName = data.path("characterName").asText();
        String guildId = data.path("guildId").asText();
        String guildName = data.path("guildName").asText();
        String adventureName = data.path("adventureName").asText();
        String jobName = data.path("jobName").asText();
        String jobGrowName = data.path("jobGrowName").asText();
        int fame = 0;
        JsonNode statusNode = data.path("status");

        for (JsonNode stat : statusNode) {
            if (stat.path("name").asText().equals("모험가 명성")) {
                fame = stat.path("value").asInt();
            }
        }

        String dataStr = null;
        String equipmentStr = null;
        String avatarStr = null;
        String creatureStr = null;
        String switchingStr = null;

        try {
            dataStr = om.writeValueAsString(data);
            equipmentStr = om.writeValueAsString(equipment);
            avatarStr = om.writeValueAsString(avatar);
            creatureStr = om.writeValueAsString(creature);
            switchingStr = om.writeValueAsString(switching);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        CharAllDTO setDto = CharAllDTO.builder()
                .characterId(characterId)
                .characterName(characterName)
                .serverId(serverId)
                .guildId(guildId)
                .guildName(guildName)
                .adventureName(adventureName)
                .jobId(jobId)
                .jobName(jobName)
                .jobGrowId(jobGrowId)
                .jobGrowName(jobGrowName)
                .fame(fame)
                .udate(udate)
                .equip(equipmentStr)
                .avatar(avatarStr)
                .creature(creatureStr)
                .data(dataStr)
                .switching(switchingStr)
                .build();

        log.info("setDTO: " + setDto);

        if (dto == null) {
            log.info("dto null");
            mapper.insertCharInfo(setDto);
            mapper.insertData(setDto);
            mapper.insertEquip(setDto);
            mapper.insertAvatar(setDto);
            mapper.insertCreature(setDto);
            mapper.insertSwitching(setDto);
        } else {
            log.info("dto update");
            mapper.updateCharInfo(setDto);
            mapper.updateData(setDto);
            mapper.updateEquip(setDto);
            mapper.updateAvatar(setDto);
            mapper.updateCreature(setDto);
            mapper.updateSwitching(setDto);
        }

        result.set("equipment", equipment);
        result.set("data", data);
        result.set("avatar", avatar);
        result.set("creature", creature);
        result.set("switching", switching);

        return result;
    }

    private ObjectNode getOneInfoFromDB(String serverId, String characterId) {

        return null;
    }

    @Override
    public ObjectNode getEquipment(String serverId, String characterId) {

        String uri = apiUrl + "servers/" + serverId + "/characters/" +
                characterId + "/equip/equipment?&apikey=" + apiKey;

        ResponseEntity<ObjectNode> res = restTemplate.getForEntity(
                uri,
                ObjectNode.class);

        ObjectNode result = res.getBody();

        result = auctionService.getEquipPrice(result);

        return result;
    }

    @Override
    public ObjectNode getCharInfo(String serverId, String characterId) {

        String uri = apiUrl + "servers/" + serverId + "/characters/" +
                characterId + "/status?apikey=" + apiKey;

        ResponseEntity<ObjectNode> res = restTemplate.getForEntity(
                uri,
                ObjectNode.class);

        ObjectNode result = res.getBody();

        return result;
    }

    @Override
    public ObjectNode getAvatar(String serverId, String characterId) {

        String uri = apiUrl + "servers/" + serverId + "/characters/" +
                characterId + "/equip/avatar?apikey=" + apiKey;

        ResponseEntity<ObjectNode> res = restTemplate.getForEntity(
                uri,
                ObjectNode.class);

        ObjectNode result = res.getBody();

        result = auctionService.getAvatarPrice(result);
        return result;
    }

    @Override
    public ObjectNode getCreature(String serverId, String characterId) {
        String uri = apiUrl + "servers/" + serverId + "/characters/" +
                characterId + "/equip/creature?apikey=" + apiKey;

        ResponseEntity<ObjectNode> res = restTemplate.getForEntity(
                uri,
                ObjectNode.class);

        ObjectNode result = res.getBody();

        result = auctionService.getCreaturePrice(result);
        return result;
    }

    @Override
    public ObjectNode getSwitching(String serverId, String characterId, String jobId) {
        JsonNode equip = switchingEquip(serverId, characterId);
        JsonNode avatar = switchingAvatar(serverId, characterId);
        JsonNode creature = switchingCreature(serverId, characterId);

        ObjectNode result = om.createObjectNode();

        result.set("equip", equip);
        result.set("avatar", avatar);
        result.set("creature", creature);

        result = auctionService.getSwitchingPrice(result, jobId);
        return result;
    }

    private JsonNode switchingEquip(String serverId, String characterId) {
        String uri = apiUrl + "servers/" + serverId + "/characters/" +
                characterId + "/skill/buff/equip/equipment?apikey=" + apiKey;

        ResponseEntity<ObjectNode> res = restTemplate.getForEntity(
                uri,
                ObjectNode.class);

        ObjectNode result = res.getBody();
        JsonNode equip = result.path("skill").path("buff").path("equipment");

        return equip;
    }

    private JsonNode switchingAvatar(String serverId, String characterId) {
        String uri = apiUrl + "servers/" + serverId + "/characters/" +
                characterId + "/skill/buff/equip/avatar?apikey=" + apiKey;

        ResponseEntity<ObjectNode> res = restTemplate.getForEntity(
                uri,
                ObjectNode.class);

        ObjectNode result = res.getBody();
        JsonNode ava = result.path("skill").path("buff").path("avatar");

        return ava;
    }

    private JsonNode switchingCreature(String serverId, String characterId) {
        String uri = apiUrl + "servers/" + serverId + "/characters/" +
                characterId + "/skill/buff/equip/creature?apikey=" + apiKey;

        ResponseEntity<ObjectNode> res = restTemplate.getForEntity(
                uri,
                ObjectNode.class);

        ObjectNode result = res.getBody();
        JsonNode creature = result.path("skill").path("buff").path("creature");

        return creature;
    }


}

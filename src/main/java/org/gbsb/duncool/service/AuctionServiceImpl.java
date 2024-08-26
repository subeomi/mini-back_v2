package org.gbsb.duncool.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.gbsb.duncool.dto.EnchantDTO;
import org.gbsb.duncool.dto.ItemInfoDTO;
import org.gbsb.duncool.dto.PriceInfoDTO;
import org.gbsb.duncool.mappers.EnchantMapper;
import org.gbsb.duncool.mappers.ItemMapper;
import org.gbsb.duncool.util.SingletonManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
@Log4j2
@RequiredArgsConstructor
public class AuctionServiceImpl implements AuctionService {

    @Value("${dnf.api.key}")
    private String apiKey;
    @Value("${dnf.api.url}")
    private String apiUrl;

    private final int limit = 20;

    private final ItemService itemService;
    private final EnchantService enchantService;
    private final ItemMapper itemMapper;

    private final EnchantMapper enchantMapper;
    private final RestTemplate restTemplate;

    private final ObjectMapper om = SingletonManager.getObjectMapper();

    @Override
    public JsonNode getEquipPrice(JsonNode equip) {

        int sumPrice = 0;
        int sumEnchantPrice = 0;

        for (JsonNode eq : equip) {
            // 장비 가격
            String slotName = eq.path("slotName").asText();
            JsonNode enchantNode = eq.path("enchant");

            if (slotName.equals("칭호")) {
                String itemId = eq.path("itemId").asText();
//                log.info("eq id: " + itemId);
                PriceInfoDTO eqAuc = getAucItemId(itemId);
//                log.info("eq auc: " + eqAuc);
                if (eqAuc != null && eqAuc.getFameRank() != 0) {
                    ((ObjectNode) eq).put("rank", eqAuc.getFameRank());
                }
                if (eqAuc != null && eqAuc.getItemPrice() != null) {
                    ((ObjectNode) eq).put("price", eqAuc.getItemPrice());
                    try {
                        ((ObjectNode) eq).set("history", om.readTree(eqAuc.getPriceHistory()));
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException(e);
                    }
                    sumPrice += Integer.parseInt(eqAuc.getItemPrice());
                }
            }

            // 여기부터 마부가격
            String enchant = enchantNode.toString();

//            log.info(slotName + "enchant: " + enchant);

            EnchantDTO dto = EnchantDTO.builder()
                    .slot(slotName)
                    .enchant(enchant)
                    .build();

//            db에서 부위와 옵션이 같은 마부정보 가져오기
            if (enchant.length() > 0) {
                dto = enchantService.getEnchant(dto);

                if (dto != null) {
//                log.info("enchant dto: " + dto);
//                db에서 itemId와 upgrade가 같은 마부의 경매장 거래기록 가져오기
                    PriceInfoDTO priceDTO = getAucEnchantDTO(dto);
//                    log.info(priceDTO);
                    if (priceDTO != null && priceDTO.getFameRank() != 0) {
                        ((ObjectNode) eq).put("enchantRank", priceDTO.getFameRank());
//                        log.info(priceDTO.getFameRank() + " rank: " + dto.getItemName());
                    } else {
                        log.info("what? priceDTO: " + priceDTO);
                        log.info("what? dto: " + dto);
                    }
                    if (priceDTO != null && priceDTO.getItemPrice() != null) {
                        ((ObjectNode) eq).put("enchantPrice", priceDTO.getItemPrice());
                        try {
                            ((ObjectNode) eq).set("enchantHistory", om.readTree(priceDTO.getPriceHistory()));
                        } catch (JsonProcessingException e) {
                            throw new RuntimeException(e);
                        }
                        sumEnchantPrice += Integer.parseInt(priceDTO.getItemPrice());
                    }
                    // 마부 객체에 마부 정보도 추가
                    ((ObjectNode) enchantNode).put("itemId", dto.getItemId());
                    ((ObjectNode) enchantNode).put("itemName", dto.getItemName());
                    ((ObjectNode) enchantNode).put("upgrade", dto.getUpgrade());
                }
            }
        }

        return equip;
    }

    @Override
    public ObjectNode getSwitchingPrice(ObjectNode switching, String jobId) {

        JsonNode equip = switching.path("equip");
        JsonNode avatar = switching.path("avatar");
        JsonNode creature = switching.path("creature");

        for (JsonNode eq : equip) {
            String itemId = eq.path("itemId").asText();
            String type = eq.path("itemTypeDetail").asText();
            if (type.equals("칭호")) {
                JsonNode jn = itemService.getReinforceItem(itemId, jobId);
                ((ObjectNode) eq).set("reinforceSkill", jn);
                continue;
            }

            PriceInfoDTO eqAuc = getAucItemId(itemId);
            if (eqAuc != null && eqAuc.getItemPrice() != null) {
                ((ObjectNode) eq).put("price", eqAuc.getItemPrice());
                try {
                    ((ObjectNode) eq).set("history", om.readTree(eqAuc.getPriceHistory()));
                } catch (JsonProcessingException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        for (JsonNode av : avatar) {
            String itemId = av.path("itemId").asText();
            JsonNode arrNode = av.path("emblems");

            if (arrNode.isArray()) {
                ArrayNode arrayNode = (ArrayNode) arrNode;
                for (int i = arrayNode.size() - 1; i >= 0; i--) {
                    if (!"플래티넘".equals(arrayNode.get(i).path("slotColor").asText())) {
                        arrayNode.remove(i);
                    }
                }
            }

            JsonNode switEmblem = av.path("emblems").get(0);
            if (switEmblem != null) {
                String switEmblemId = switEmblem.path("itemId").asText();

                PriceInfoDTO emblemAuc = getAucItemId(switEmblemId);

                if (emblemAuc != null && emblemAuc.getItemPrice() != null) {
                    ((ObjectNode) switEmblem).put("price", emblemAuc.getItemPrice());
                    try {
                        ((ObjectNode) switEmblem).set("history", om.readTree(emblemAuc.getPriceHistory()));
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException(e);
                    }
                }
            }

            PriceInfoDTO avAuc = getAucItemId(itemId);

            if (avAuc != null && avAuc.getItemPrice() != null) {
                ((ObjectNode) av).put("price", avAuc.getItemPrice());
                try {
                    ((ObjectNode) av).set("history", om.readTree(avAuc.getPriceHistory()));
                } catch (JsonProcessingException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        if (creature != null) {
            for (JsonNode cr : creature) {
                String crItemId = cr.path("itemId").asText();
                JsonNode jn = itemService.getReinforceItem(crItemId, jobId);
                ((ObjectNode) cr).put("slotName", "크리쳐");
                ((ObjectNode) cr).set("reinforceSkill", jn);
            }
        }

        return switching;
    }

    @Override
    public JsonNode getAvatarPrice(JsonNode avatar) {

        for (JsonNode av : avatar) {
            String itemId = av.path("itemId").asText();
            JsonNode clone = av.path("clone");
            JsonNode emblems = av.path("emblems");
            String slotName = av.path("slotName").asText();

            // 메인아바타
            PriceInfoDTO avAuc = getAucItemId(itemId);
            if (slotName.equals("오라 아바타")) {
//            log.info(avAuc);
                if (avAuc != null && avAuc.getFameRank() != 0) {
                    ((ObjectNode) av).put("rank", avAuc.getFameRank());
                }
            }
            if (avAuc != null && avAuc.getItemPrice() != null) {
                ((ObjectNode) av).put("price", avAuc.getItemPrice());
                try {
                    ((ObjectNode) av).set("history", om.readTree(avAuc.getPriceHistory()));
                } catch (JsonProcessingException e) {
                    throw new RuntimeException(e);
                }
            }
            // 클론아바타
            if (clone.path("itemId").asText() != null) {
                PriceInfoDTO cloneAuc = getAucItemId(clone.path("itemId").asText());
                if (cloneAuc != null && cloneAuc.getItemPrice() != null) {
                    ((ObjectNode) clone).put("price", cloneAuc.getItemPrice());
                    try {
                        ((ObjectNode) clone).set("history", om.readTree(cloneAuc.getPriceHistory()));
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
            // 엠블렘
            if (emblems != null) {
                for (JsonNode em : emblems) {
//                    log.info(em);
                    PriceInfoDTO emblemAuc = getAucItemId(em.path("itemId").asText());
                    if (emblemAuc != null && emblemAuc.getItemPrice() != null) {
                        ((ObjectNode) em).put("price", emblemAuc.getItemPrice());
                        ((ObjectNode) em).put("rank", emblemAuc.getFameRank());
                        try {
                            ((ObjectNode) em).set("history", om.readTree(emblemAuc.getPriceHistory()));
                        } catch (JsonProcessingException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
            }
        }

        return avatar;
    }

    @Override
    public JsonNode getCreaturePrice(JsonNode creature) {

        JsonNode art = creature.path("artifact");
        String creatureName = creature.path("itemName").asText();
        String chkId = creature.path("itemId").asText();
        String aucItemId = null;

        if (creatureName != null) {
            String creatureId = getEggItemId(creatureName);
            if (creatureId == null) creatureId = chkId;

            PriceInfoDTO crAuc = getAucItemId(creatureId);

            if (crAuc != null && crAuc.getItemPrice() != null) {
                ((ObjectNode) creature).put("price", crAuc.getItemPrice());
                try {
                    ((ObjectNode) creature).set("history", om.readTree(crAuc.getPriceHistory()));
                } catch (JsonProcessingException e) {
                    throw new RuntimeException(e);
                }
            }
//          가격 정보가 없어라도 명성 순위는 가져와야 한다
            if (crAuc != null && crAuc.getFameRank() >= 0) {
                ((ObjectNode) creature).put("rank", crAuc.getFameRank());
            }
        }

        for (JsonNode ar : art) {
            String itemId = ar.path("itemId").asText();

            PriceInfoDTO arAuc = getAucItemId(itemId);
            if (arAuc != null && arAuc.getItemPrice() != null) {
                ((ObjectNode) ar).put("price", arAuc.getItemPrice());
                ((ObjectNode) ar).put("rank", arAuc.getFameRank());
                try {
                    ((ObjectNode) ar).set("history", om.readTree(arAuc.getPriceHistory()));
                } catch (JsonProcessingException e) {
                    throw new RuntimeException(e);
                }
            }
        }


        return creature;
    }

    @Override
    public PriceInfoDTO getAucItemId(String itemId) {
        PriceInfoDTO dto = itemMapper.getOnePrice(itemId);
//        log.info("get one price itemId: " + itemId);
//        log.info(dto);

        String pdate = null;
        Long dateDiff = null;
        int fameRank = 0;

        if (dto != null && dto.getPdate() != null) {
            pdate = dto.getPdate();

            LocalDateTime now = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            LocalDateTime dateTime = LocalDateTime.parse(pdate, formatter);

            Duration duration = Duration.between(dateTime, now);
            dateDiff = duration.toDays();
            //fame_rank값이 있으면 저장
            if (dto.getFameRank() > 0) {
                fameRank = dto.getFameRank();
            }
//            log.info("pdate: " + pdate + ", dateTime: " + dateTime + ", diff: " + dateDiff);
        }

        if (dto == null || (dateDiff != null && dateDiff >= 0) || (dto != null && dto.getItemId() == null) || dto.getFameRank() == 0) {
            JsonNode price = createPrice(itemId);
//            log.info(price);

            if(price != null){
                dto = dto.builder()
                        .priceHistory(price.path("priceHistory").asText())
                        .itemPrice(price.path("itemPrice").asText())
                        .itemId(price.path("itemId").asText())
                        .itemName(price.path("itemName").asText()).build();
                itemMapper.insertPriceInfo(dto);
            }

//            log.info("insert price dto: " + dto);

            if (fameRank > 0) {
                dto.setFameRank(fameRank);
            } else {
                dto = itemMapper.getOnePrice(itemId);
            }
        }

//        log.info("aucPrice: " + dto);

        return dto;
    }

    @Override
    public PriceInfoDTO getAucEnchantDTO(EnchantDTO enchantDTO) {
        String itemId = enchantDTO.getItemId();
//        log.info("getAucEnchantDTO id : " + itemId);

        PriceInfoDTO dto = itemMapper.getOnePrice(itemId);
//        log.info("get enchant price : " + dto);

        String pdate = null;
        Long dateDiff = null;
        int fameRank = 0;

        if (dto != null && dto.getPdate() != null) {
            pdate = dto.getPdate();

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            LocalDateTime dateTime = LocalDateTime.parse(pdate, formatter);
            LocalDateTime now = LocalDateTime.now();

            Duration duration = Duration.between(dateTime, now);
            dateDiff = duration.toDays();

//            fame_rank값이 있으면 저장
            if (dto.getFameRank() > 0) {
                fameRank = dto.getFameRank();
            }
//            log.info("pdate: " + pdate + ", dateTime: " + dateTime + ", diff: " + dateDiff);
        }

        if (dto == null || (dateDiff != null && dateDiff >= 1) || (dto != null && dto.getItemId() == null)) {
//            JsonNode price = createEnchantPrice(enchantDTO);
//            24-06-02 현재 경매장 데이터에서 upgrade를 안 줌
//            log.info(itemId);
            JsonNode price = createPrice(itemId);
//            log.info("enchant price" + price);

            if(price != null){
                dto = dto.builder()
                        .priceHistory(price.path("priceHistory").asText())
                        .itemPrice(price.path("itemPrice").asText())
                        .itemId(price.path("itemId").asText())
                        .itemName(price.path("itemName").asText()).build();
                itemMapper.insertPriceInfo(dto);
            }

//            log.info("insert price dto: " + dto);
            if (fameRank > 0) {
                dto.setFameRank(fameRank);
            } else {
                dto = itemMapper.getOnePrice(itemId);
            }
        }

//        log.info("aucPrice: " + dto);

        return dto;
    }

    private JsonNode createPrice(String itemId) {
//        log.info("auc search itemId: " + itemId);
        ItemInfoDTO itemDTO = itemService.getOneItem(itemId);
//        log.info(itemDTO);
        if (itemDTO == null) return null;

        String auctionSoldUri = apiUrl + "auction-sold?itemId=" +
                itemId + "&limit=" + limit + "&apikey=" + apiKey;

        ResponseEntity<ObjectNode> aucRes = restTemplate.getForEntity(
                auctionSoldUri,
                ObjectNode.class);

        ObjectNode result = aucRes.getBody();

        JsonNode arr = result.path("rows");

        if (arr.isEmpty()) return null;
//        log.info("auc arr: " + arr);

        ObjectNode price = om.createObjectNode();
        ArrayNode hisArr = om.createArrayNode();

        String itemName = itemDTO.getItemName();

        long sumPrice = 0;
        int count = 0;
        long initialAverage = 0;

        if (!arr.isEmpty()) {
            for (JsonNode item : arr) {
                if (count < 5) {
                    ObjectNode newObj = om.createObjectNode();
                    newObj.put("unitPrice", item.get("unitPrice").asLong());
                    newObj.put("soldDate", item.get("soldDate").asText());
                    hisArr.add(newObj);
                }
                sumPrice += item.path("unitPrice").asLong();
                count++;
            }
            initialAverage = sumPrice / count;

        }

        // 이상치 제거 후 평균 재계산
        sumPrice = 0;
        count = 0;
        for (JsonNode item : arr) {
            long unitPrice = item.path("unitPrice").asLong();
            if (unitPrice <= initialAverage * 2) { // 평균의 2배 이하인 경우만 포함
                sumPrice += unitPrice;
                count++;
            }
        }
        long finalAverage = count > 0 ? sumPrice / count : 0;

        String hisArrStr = hisArr.toString();

//        log.info("hisArr: {}", hisArr);
//        log.info("hisArrStr: {}", hisArrStr);

        price.put("itemId", itemId);
        price.put("itemName", itemName);
        price.put("itemPrice", finalAverage);
        price.put("priceHistory", hisArrStr);

//        log.info("price : " + price);

        return price;
    }

    private JsonNode createEnchantPrice(EnchantDTO dto) {
//        log.info("auc search itemId: " + itemId);
        String itemId = dto.getItemId();
        int upgrade = Integer.parseInt(dto.getUpgrade());

        ItemInfoDTO itemDTO = itemService.getOneItem(itemId);
//        log.info("itemId: "+itemId);

        String auctionSoldUri = apiUrl + "auction-sold?itemId=" +
                itemId + "&limit=100&apikey=" + apiKey;

        ResponseEntity<ObjectNode> aucRes = restTemplate.getForEntity(
                auctionSoldUri,
                ObjectNode.class);

        ObjectNode result = aucRes.getBody();

        JsonNode arr = result.path("rows");

        if (arr.isEmpty()) return null;
        log.info("enc auc arr: " + arr);

        ObjectNode price = om.createObjectNode();
        ArrayNode hisArr = om.createArrayNode();

        String itemName = itemDTO.getItemName();

        long sumPrice = 0;
        int count = 0;
        long initialAverage = 0;

        for (JsonNode item : arr) {
            if (item.path("upgrade").asInt() == upgrade) {
                if (count < 5) {
                    ObjectNode newObj = om.createObjectNode();
                    newObj.put("unitPrice", item.get("unitPrice").asLong());
                    newObj.put("soldDate", item.get("soldDate").asText());
                    hisArr.add(newObj);
                }
                sumPrice += item.path("unitPrice").asLong();
                count++;
                if (count > 19) break;
            }
        }
//        log.info("count: " + count);

        if (count == 0) return null;
        initialAverage = sumPrice / count;


        // 이상치 제거 후 평균 재계산
        sumPrice = 0;
        count = 0;
        for (JsonNode item : arr) {
            if (item.path("upgrade").asInt() == upgrade) {
                long unitPrice = item.path("unitPrice").asLong();
                if (unitPrice <= initialAverage * 2) { // 평균의 2배 이하인 경우만 포함
                    sumPrice += unitPrice;
                    count++;
                    if (count > 19) break;
                }
            }
        }
        long finalAverage = count > 0 ? sumPrice / count : 0;

        price.put("itemId", itemId);
        price.put("itemName", itemName);
        price.put("itemPrice", finalAverage);
        price.set("priceHistory", hisArr);

        return price;
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
        } else if (creatureName.contains("순백의 나비 공주")) {
            aucItemId = "701574f4768183cd7d8fee2774de2a93";
        } else if (creatureName.contains("축제의 여왕 페리아")) {
            aucItemId = "87a131e3e8ab68b0e3ff5ea80d9e8975";
        }

        return aucItemId;
    }
}

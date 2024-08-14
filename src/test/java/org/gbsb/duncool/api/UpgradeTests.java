package org.gbsb.duncool.api;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.extern.log4j.Log4j2;
import org.gbsb.duncool.dto.ItemInfoDTO;
import org.gbsb.duncool.dto.PriceInfoDTO;
import org.gbsb.duncool.service.AuctionService;
import org.gbsb.duncool.service.ItemService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@SpringBootTest
@Log4j2
public class UpgradeTests {

    private final String API_KEY = "5KbFU6jtQwi5dwY7TXUHaSutPZlTM9pQ";
    private final String API_URL = "https://api.neople.co.kr/df/";

    private String 카시야스 = "casillas";
    private String 바칼 = "bakal";
    private String 카인 = "cain";
    private String 디레지에 = "diregie";

    private final String 븝프 = "4b982ea11e3fc13bbe4100cae2cc5c9e";
    private final String 븜미맨 = "33ef61758bc45e20ac5082f8009e8382"; // 서버 바칼
    private final String 독린이 = "03d52acdd399ba6d447ce3f3525c5bc6"; // 서버 바칼
    private final String 깨시민 = "ba24a0025ad3fb1c72d26f2a079f7cc0";
    private final String 핑핑이수프 = "d0dde5fcd22636b9e6de2f2fa81da722"; // 서버 카인
    private final String 윤상준 = "62a5b5e9beb57001710d053c6f7a039d"; // 서버 디레지에
    private final String 현무 = "a601847624f85d5a810897090c6f2fdc"; // 서버 디레지에
    private final String 중독맘 = "b9a8ad051a9caf594ed5219bf5a4e917"; // 서버 디레지에
    private final String 도시란 = "1e7dae78a37d88e50acac5ce22014299"; // 서버 디레지에
    private final String 비극 = "e13d3d808a989bda8aebf0d12ee9fe25"; // 서버 디레지에

    private final int 강화 = 401;
    private final int 증폭 = 402;
    private final int 봉자 = 501;
    private final int 레이드 = 201;

    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private AuctionService auctionService;
    @Autowired
    private ItemService itemService;

    @Test
    public void apiTimelineTest() {
        // 시작 날짜와 끝 날짜 설정
        LocalDateTime startDate = LocalDateTime.of(2018, 8, 9, 6, 0);
        LocalDateTime endDate = LocalDateTime.now();

        // API 호출을 위한 기본 URL 설정
        String baseURL = API_URL + "servers/" + 디레지에 + "/characters/" + 중독맘 + "/timeline";

        // DateTimeFormatter를 사용하여 날짜를 문자열로 변환
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

        ObjectMapper om = new ObjectMapper();
        ArrayNode arr = om.createArrayNode();

        // 3개월씩 반복하면서 API 호출
        while (startDate.isBefore(endDate)) {
            // 다음 3개월 날짜 계산
            LocalDateTime nextStartDate = startDate.plusMonths(3);
            LocalDateTime nextEndDate = startDate.plusDays(90).minusMinutes(1); // 시작일로부터 90일 후 마지막 시간

            if (nextEndDate.isAfter(endDate)) {
                nextEndDate = endDate;
            }

            // API 호출에 사용할 startDate 및 endDate 문자열 생성
            String startDateStr = startDate.format(formatter);
            String endDateStr = nextEndDate.format(formatter); // 23:59로 설정

//            // API 호출
//            String upgradeURL = baseURL + "?next=&limit=100&code="+강화+"&startDate=" + startDateStr + "&endDate=" + endDateStr + "&apikey=" + API_KEY;
//            ResponseEntity<ObjectNode> upgradeRes = restTemplate.getForEntity(
//                    upgradeURL,
//                    ObjectNode.class);
//            ObjectNode upgrades = upgradeRes.getBody();
//            JsonNode upgrade = upgrades.path("timeline").path("rows");

            String forgeURL = baseURL + "?limit=100&code=" + 레이드 + "&startDate=" + startDateStr + "&endDate=" + endDateStr + "&apikey=" + API_KEY;
            ResponseEntity<ObjectNode> forgeRes = restTemplate.getForEntity(
                    forgeURL,
                    ObjectNode.class);
            ObjectNode forges = forgeRes.getBody();

            log.info(forges);

            JsonNode forge = forges.path("timeline").path("rows");
            log.info("length: " + forge.size());

//            if (upgrade.isArray()) {
//                for (JsonNode up : upgrade) {
//                    JsonNode data = up.path("data").path("ticket");
//                    if (data != null && !data.equals("null") && !data.isEmpty() && data.isObject()) {
//                        log.info("up data: " + data);
//                        arr.add(data);
//                    }
//                }
//            }

            if (forge.isArray()) {
                for (JsonNode fo : forge) {
                    if (fo != null) {
//                        log.info("fo data: " + data);
//                        fo = calForgeCost(fo);
                        arr.add(fo);
                    }
                }
            }

            // 다음 호출을 위해 startDate 갱신
            startDate = nextStartDate;
        }

        log.info("arr size: " + arr.size());
        log.info("arr: " + arr);

//        int sumPrice = 0;
//        int sumGold = 0;
//        int sumMaterial = 0;
//
//        for (JsonNode ar : arr) {
//            int gold = ar.path("gold").asInt();
//            int price = ar.path("price").asInt();
//            int material = ar.path("material").asInt();
//
//            sumPrice += price;
//            sumGold += gold;
//            sumMaterial += material;
//        }
//
//        PriceInfoDTO golgoDTO = auctionService.getAucItemId("f1afc13118b2b07ec1e3b8c2f1958b03");
//        int golgo = Integer.parseInt(golgoDTO.getItemPrice());
//
//        log.info("sumPrice: {}", sumPrice);
//        log.info("sumGold: {}", sumGold);
//        log.info("sumMaterial: {}", sumMaterial);
//        log.info("sumMaterialPrice: {}", sumMaterial * golgo);
//        log.info("==========================================================");
//        log.info("증폭권 가격 합계: "+ sumPrice);
//        log.info("골드 비용 합계: "+ sumGold);
//        log.info("모순 갯수 합계: "+ sumMaterial);
//        log.info("모순 비용 합계: "+ sumMaterial * golgo);
    }

    private JsonNode calForgeCost(JsonNode forge) {

        JsonNode data = forge.path("data");
        String itemId = data.path("itemId").asText();
        int before = data.path("before").asInt();
        int after = data.path("after").asInt();
//        log.info(forge);

        ItemInfoDTO itemInfoDTO = itemService.getOneItem(itemId);
        int itemLevel = itemInfoDTO.getItemAvailableLevel();
        String itemName = itemInfoDTO.getItemName();
//        if (itemLevel < 95 || !itemName.equals("장비 증폭권[골고라이언]")) return forge;

        int material = 0;
        int gold = 0;
        JsonNode ticketNode = data.get("ticket");
//        log.info(itemInfoDTO.getItemName());

        if (ticketNode != null && !ticketNode.isNull()) {
            String ticketId = ticketNode.path("itemId").asText();
            PriceInfoDTO priceInfoDTO = auctionService.getAucItemId(ticketId);

            if (priceInfoDTO == null) return forge;
            gold = Integer.parseInt(priceInfoDTO.getItemPrice());
            ((ObjectNode) forge).put("price", gold);
        } else if (after - before == 1) {
            if (itemLevel >= 95) {
                if (itemInfoDTO.getItemType().equals("무기")) {
                    gold = 739200;
                    material = after;
                    ((ObjectNode) forge).put("gold", gold);
                    ((ObjectNode) forge).put("material", material);
                } else {
                    gold = 258720;
                    material = after;
                    ((ObjectNode) forge).put("gold", gold);
                    ((ObjectNode) forge).put("material", material);
                }
            } else if (itemName.equals("장비 증폭권[골고라이언]")) {
                gold = 418880;
                material = after;
                ((ObjectNode) forge).put("gold", gold);
                ((ObjectNode) forge).put("material", material);
            }
        }


        return forge;
    }
}

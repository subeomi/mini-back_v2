package org.gbsb.duncool.api;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.Builder;
import lombok.extern.log4j.Log4j2;
import org.gbsb.duncool.dto.ItemInfoDTO;
import org.gbsb.duncool.dto.TradeDTO;
import org.gbsb.duncool.mappers.EnchantMapper;
import org.gbsb.duncool.mappers.ItemMapper;
import org.gbsb.duncool.service.EnchantService;
import org.gbsb.duncool.service.ItemService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@SpringBootTest
@Log4j2
public class AuctionTests {

    @Value("${dnf.api.key}")
    private String API_KEY;
    @Value("${dnf.api.url}")
    private String API_URL;

    private final int limit = 20;

    @Autowired
    private ItemService itemService;
    @Autowired
    private EnchantService enchantService;
    @Autowired
    private ItemMapper itemMapper;

    @Autowired
    private EnchantMapper enchantMapper;
    @Autowired
    private RestTemplate restTemplate;

    //    진정한 각성을 이룬 자 플래티넘[35Lv]
    String 진각35 = "7a7fd418aa015274e75af4b7d24db702";
    String 골고11 = "fac4ce61d490d3a006025c797abb5950";

    @Test
    public void getOnePriceTest() {

        String itemId = 진각35;

        String auctionSoldUri = API_URL + "auction-sold?itemId=" +
                itemId + "&limit=" + limit + "&apikey=" + API_KEY;

        ResponseEntity<ObjectNode> aucRes = restTemplate.getForEntity(
                auctionSoldUri,
                ObjectNode.class);

        ObjectNode result = aucRes.getBody();

        JsonNode arr = result.path("rows");

        log.info("auc arr: " + arr);

        ObjectMapper om = new ObjectMapper();
        List<ObjectNode> extractedList = new ArrayList<>();

        long sumPrice = 0;
        int count = 0;
        long initialAverage = 0;

        if (!arr.isEmpty()) {
            for (JsonNode item : arr) {
                if (count < 5) {
                    ObjectNode newObj = om.createObjectNode();
                    newObj.put("unitPrice", item.get("unitPrice").asLong());
                    newObj.put("soldDate", item.get("soldDate").asText());
                    extractedList.add(newObj);
                }
                log.info(item.path("unitPrice").asLong());
                count++;
            }
        }

        log.info(extractedList);

        // 이상치 제거 후 평균 재계산
        sumPrice = 0;
        count = 0;
        log.info("==============");
        log.info("initAvg: " + initialAverage);
        for (JsonNode item : arr) {
            long unitPrice = item.path("unitPrice").asLong();
            if (unitPrice <= initialAverage * 2) { // 평균의 2배 이하인 경우만 포함
                sumPrice += unitPrice;
                count++;
            }
        }
        long finalAverage = count > 0 ? sumPrice / count : 0;

        log.info("평균가: " + finalAverage);
    }


    //    ???????????????????????????? 보류
    @Test
    public void tradeInsertTest() {
        String itemId = 진각35;

        String auctionSoldUri = API_URL + "auction-sold?itemId=" +
                itemId + "&limit=" + limit + "&apikey=" + API_KEY;

        ResponseEntity<ObjectNode> aucRes = restTemplate.getForEntity(
                auctionSoldUri,
                ObjectNode.class);

        ObjectNode result = aucRes.getBody();

        JsonNode arr = result.path("rows");

        log.info("auc arr: " + arr);

        ObjectMapper om = new ObjectMapper();

        TradeDTO dto = itemMapper.getRecentOneTrade(itemId);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime oneDate = LocalDateTime.parse(dto.getTradeDate(), formatter);

        log.info("기준: " + oneDate);

        if (!arr.isEmpty()) {
            for (JsonNode item : arr) {
                LocalDateTime soldDate = LocalDateTime.parse(item.path("soldDate").asText(), formatter);
                log.info(soldDate.isAfter(oneDate) + " " + soldDate);
                if (soldDate.isAfter(oneDate)) {
                    TradeDTO trade = TradeDTO.builder()
                            .itemId(item.path("itemId").asText())
                            .itemPrice(item.path("unitPrice").asLong())
                            .tradeDate(item.path("soldDate").asText())
                            .build();
                    itemMapper.insertTradeHistory(trade);
                }
            }
        }
    }

    @Test
    public void tradeTest1() {
//        가장 최근 데이터 체크
        String itemId = 진각35;
        TradeDTO dto = itemMapper.getRecentOneTrade(itemId);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

//        가장 최근기록 있으면 24시간 전인지 확인
        if (dto != null) {
            LocalDateTime oneDate = LocalDateTime.parse(dto.getTradeDate(), formatter);
            LocalDateTime now = LocalDateTime.now();
            Duration duration = Duration.between(oneDate, now);
            long dateDiff = duration.toHours();
            log.info(dateDiff);

//            최근기록이 24시간 전이면 새 데이터 호출
            if (dateDiff > 23) {
                JsonNode arr = getAucHistory(itemId);
                for (JsonNode item : arr) {
                    LocalDateTime itemDate = LocalDateTime.parse(item.path("soldDate").asText(), formatter);
//            oneDate(기준시간)가 itemDate 이후인지 확인
                    log.info(itemDate.isAfter(oneDate) + " : " + itemDate);
                    if (itemDate.isAfter(oneDate)) {
//                    데이터 삽입
                        TradeDTO tradeDTO = TradeDTO.builder()
                                .itemId(item.path("itemId").asText())
                                .itemPrice(item.path("unitPrice").asLong())
                                .tradeDate(item.path("soldDate").asText())
                                .build();

                        itemMapper.insertTradeHistory(tradeDTO);
                    }
                }
            }
        } else {
//            기록 없으면 데이터 싹 다 삽입
            JsonNode arr = getAucHistory(itemId);

            for (JsonNode item : arr) {
//                    데이터 삽입
                TradeDTO tradeDTO = TradeDTO.builder()
                        .itemId(item.path("itemId").asText())
                        .itemPrice(item.path("unitPrice").asLong())
                        .tradeDate(item.path("soldDate").asText())
                        .build();

                itemMapper.insertTradeHistory(tradeDTO);
            }
        }

        log.info(dto);
    }

    private JsonNode getAucHistory(String itemId) {

        String auctionSoldUri = API_URL + "auction-sold?itemId=" +
                itemId + "&limit=" + limit + "&apikey=" + API_KEY;

        ResponseEntity<ObjectNode> aucRes = restTemplate.getForEntity(
                auctionSoldUri,
                ObjectNode.class);

        ObjectNode result = aucRes.getBody();

        JsonNode arr = result.path("rows");

        return arr;
    }
}

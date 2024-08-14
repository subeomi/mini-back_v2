package org.gbsb.duncool.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.gbsb.duncool.dto.CharAllDTO;
import org.gbsb.duncool.dto.CharInfoDTO;
import org.gbsb.duncool.mappers.InfoMapper;
import org.gbsb.duncool.service.DuncoolService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@SpringBootTest
@RequiredArgsConstructor
@Log4j2
public class ApiTests {

    private final String API_KEY = "5KbFU6jtQwi5dwY7TXUHaSutPZlTM9pQ";
    private final String API_URL = "https://api.neople.co.kr/df/";

    @Autowired
    private InfoMapper mapper;
    @Autowired
    private DuncoolService duncoolService;
    @Autowired
    private RestTemplate restTemplate;


    // private final CharSearchDTO dto;

    @Test
    public void apiTest1() {
        String uri = API_URL + "servers/" + "cain" + "/characters/" +
                "b180c92a11436899d289de2c1ee6149d" + "/status?apikey=" + API_KEY;

        ResponseEntity<ObjectNode> res = restTemplate.getForEntity(
                uri,
                ObjectNode.class);

        ObjectNode result = res.getBody();

        String characterId = result.path("characterId").asText();

        CharInfoDTO dto = mapper.getOneInfo("a8fde495dfe7109b23492faa4da0c53b");

        log.info(result);
        log.info(dto);
    }

    @Test
    public void apiTimelineTest() {
        // 시작 날짜와 끝 날짜 설정
        LocalDateTime startDate = LocalDateTime.of(2022, 10, 7, 0, 0);
        LocalDateTime endDate = LocalDateTime.now();

        // API 호출을 위한 기본 URL 설정
        String baseURL = API_URL + "servers/casillas/characters/ba24a0025ad3fb1c72d26f2a079f7cc0/timeline";

        // DateTimeFormatter를 사용하여 날짜를 문자열로 변환
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

        ArrayList<String> arr = new ArrayList<>();

        // 3개월씩 반복하면서 API 호출
        // 다음 3개월 날짜 계산
        LocalDateTime nextStartDate = startDate.plusMonths(3);
        LocalDateTime nextEndDate = startDate.plusDays(90).minusMinutes(1); // 시작일로부터 90일 후 마지막 시간

        if (nextEndDate.isAfter(endDate)) {
            nextEndDate = endDate;
        }

        // API 호출에 사용할 startDate 및 endDate 문자열 생성
        String startDateStr = startDate.format(formatter);
        String endDateStr = nextEndDate.format(formatter); // 23:59로 설정

        // API 호출
        String apiURL = baseURL + "?limit=100&code=209&startDate=" + startDateStr + "&endDate=" + endDateStr + "&apikey=" + API_KEY;
        log.info("API URL: " + apiURL);
        ResponseEntity<ObjectNode> responseEntity = restTemplate.getForEntity(
                apiURL,
                ObjectNode.class);

        ObjectNode result = responseEntity.getBody();

        JsonNode region = result.path("timeline").path("rows");
        log.info(region);

        if (region.isArray()) {
            for (JsonNode rg : region) {
                String data = rg.path("data").path("regionName").asText();
                log.info(data);
                arr.add(data);
            }
        }

        log.info("API RESPONSE: " + result);

        // 다음 호출을 위해 startDate 갱신
        startDate = nextStartDate;


        log.info(arr);
    }

    // 레이드 레기온 숫자와 최근1달 공대 정보
    @Test
    public void apiTimelineTest2() {
        // 시작 날짜와 끝 날짜 설정
        LocalDateTime startDate = LocalDateTime.of(2022, 7, 7, 0, 0);
        LocalDateTime endDate = LocalDateTime.now();

        // API 호출을 위한 기본 URL 설정
        String baseURL = API_URL + "servers/casillas/characters/ba24a0025ad3fb1c72d26f2a079f7cc0/timeline";

        // DateTimeFormatter를 사용하여 날짜를 문자열로 변환
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

        ArrayList<String> arr = new ArrayList<>();

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

            // API 호출
            String regionURL = baseURL + "?next=&limit=100&code=209&startDate=" + startDateStr + "&endDate=" + endDateStr + "&apikey=" + API_KEY;
            String raidURL = baseURL + "?next=&limit=100&code=201&startDate=" + startDateStr + "&endDate=" + endDateStr + "&apikey=" + API_KEY;
//            log.info(apiURL);
            ResponseEntity<ObjectNode> regionRes = restTemplate.getForEntity(
                    regionURL,
                    ObjectNode.class);
            ResponseEntity<ObjectNode> raidRes = restTemplate.getForEntity(
                    raidURL,
                    ObjectNode.class);

            ObjectNode regions = regionRes.getBody();
            ObjectNode raids = raidRes.getBody();

            log.info(raids);

            JsonNode region = regions.path("timeline").path("rows");
            JsonNode raid = raids.path("timeline").path("rows");

            if (region.isArray()) {
                for (JsonNode rg : region) {
                    String data = rg.path("data").path("regionName").asText();
                    arr.add(data);
                }
            }

            if (raid.isArray()) {
                for (JsonNode rd : raid) {
                    String data = rd.path("data").path("modeName").asText();
                    if (!data.isEmpty() && !data.equals("개전")) arr.add(data);
                }
            }

            // 다음 호출을 위해 startDate 갱신
            startDate = nextStartDate;
        }

        log.info(arr);

        Map<String, Integer> raidCounts = new HashMap<>();

        for (String raidName : arr) {
            raidCounts.put(raidName, raidCounts.getOrDefault(raidName, 0) + 1);
        }

        log.info(raidCounts.entrySet());
        log.info(raidCounts);

        for (Map.Entry<String, Integer> entry : raidCounts.entrySet()) {
            log.info(entry.getKey() + ": " + entry.getValue() + "회");
        }
    }

    // 리스트데이터
    @Test
    public void apiSearchFameTest2() {
        String baseURL = API_URL + "servers/all/characters-fame?";

        String URL = baseURL + "limit=200&apikey=" + API_KEY;

        ResponseEntity<ObjectNode> fameRes = restTemplate.getForEntity(
                URL,
                ObjectNode.class);

        ObjectNode res = fameRes.getBody();

        JsonNode arr = res.path("rows");
        log.info(res);
        log.info(arr.size());

        JsonNode firstNode = arr.get(0);
        String serverId = firstNode.path("serverId").asText();
        String characterId = firstNode.path("characterId").asText();

        ObjectNode data = duncoolService.getCharInfo(serverId, characterId);
        ObjectNode equipment = duncoolService.getEquipment(serverId, characterId);
        ObjectNode avatar = duncoolService.getAvatar(serverId, characterId);

        log.info(data);

        String characterName = data.path("characterName").asText();
        String guildId = data.path("guildId").asText();
        String guildName = data.path("guildName").asText();
        String adventureName = data.path("adventureName").asText();

        CharAllDTO dto = CharAllDTO
                .builder()
                .characterId(characterId)
                .characterName(characterName)
                .guildId(guildId)
                .guildName(guildName)
                .adventureName(adventureName)
                .build();

        log.info(dto);
        mapper.insertCharInfo(dto);

//        data.remove("buff");
//        data.remove("status");
//        ObjectNode newData = data.objectNode();
//        newData.setAll(data);
//        newData.setAll(title);
//        log.info("-==================-");
//        log.info(newData);

    }

}

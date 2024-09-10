package org.gbsb.duncool;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.extern.log4j.Log4j2;
import org.gbsb.duncool.service.DuncoolService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@Log4j2
class DuncoolApplicationTests {

	private final String MY_SERVER = "casillas";
	private final String MY_CHAR = "ba24a0025ad3fb1c72d26f2a079f7cc0";

	@Value("${dnf.api.key}")
	private String apiKey;

	@Autowired
	DuncoolService duncoolService;

	@Test
	public void test1() {
		JsonNode test1 = duncoolService.getEquipment(MY_SERVER,MY_CHAR);
		log.info(test1);
	}

	@Test
	public void test2() {
		log.info(apiKey);
	}

}
